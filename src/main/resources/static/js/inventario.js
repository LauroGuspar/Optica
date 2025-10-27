$(document).ready(function() {
    let dataTable;
    let movimientosModal;
    let movimientosDataTable; // Variable para la DataTable de movimientos

    const API_ENDPOINTS = {
        productos: '/productos/api/listar',
        movimientos: (id) => `/inventario/api/movimientos/${id}`,
        exportPdf: (id) => `/inventario/api/movimientos/pdf/${id}`
    };

    initializeDataTable();
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaInventario').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: API_ENDPOINTS.productos,
                dataSrc: 'data'
            },
            columns: [
                { data: 'nombre' },
                {
                    data: 'stock',
                    className: 'text-center',
                    render: function(data, type, row) {
                        if (data <= row.stockMinimo) {
                            return `<span class="badge text-bg-danger">${data}</span>`;
                        }
                        return `<span class="badge text-bg-success">${data}</span>`;
                    }
                },
                { data: 'stockMinimo', className: 'text-center' },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    className: 'text-center',
                    render: function(data, type, row) {
                        return `<button class="btn btn-info btn-sm action-movimientos" data-id="${row.id}" data-nombre="${row.nombre}" title="Ver Movimientos">
                                    <i class="fas fa-chart-line"></i> Movimientos
                                </button>`;
                    }
                }
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" },
            order: [[1, 'asc']] // Ordenar por stock ascendente por defecto
        });
    }

    function setupEventListeners() {
        $('#tablaInventario tbody').on('click', '.action-movimientos', handleShowMovements);
    }

    function handleShowMovements() {
        const productoId = $(this).data('id');
        const productoNombre = $(this).data('nombre');

        AppUtils.showLoading(true);

        fetch(API_ENDPOINTS.movimientos(productoId))
            .then(response => {
                if (!response.ok) throw new Error('No se pudieron cargar los movimientos.');
                return response.json();
            })
            .then(data => {
                populateMovementsModal(data, productoNombre, productoId);
                if (!movimientosModal) {
                    movimientosModal = new bootstrap.Modal(document.getElementById('modalMovimientos'));
                }
                movimientosModal.show();
            })
            .catch(error => AppUtils.showNotification(error.message, 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function populateMovementsModal(data, productoNombre, productoId) {
        $('#nombreProductoMovimiento').text(`Producto: ${productoNombre}`);

        // Si la DataTable de movimientos ya existe, la destruimos para reinicializarla.
        if (movimientosDataTable) {
            movimientosDataTable.destroy();
        }

        // Inicializamos la DataTable dentro del modal
        movimientosDataTable = $('#tablaMovimientos').DataTable({
            data: data.movimientos, // Usamos los datos directamente
            responsive: true,
            dom: 'Bfrtip', // 'B' para los botones
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel"></i> Exportar a Excel',
                    className: 'btn btn-success',
                    title: `Movimientos - ${productoNombre}`
                },
                {
                    extend: 'pdfHtml5',
                    text: '<i class="fas fa-file-pdf"></i> Exportar a PDF',
                    className: 'btn btn-danger',
                    title: `Movimientos - ${productoNombre}`,
                    customize: function (doc) {
                        // --- 1. Personalización General del Documento ---
                        doc.pageMargins = [40, 60, 40, 60]; // [izquierda, arriba, derecha, abajo]
                        doc.defaultStyle.fontSize = 10;
                        doc.styles.tableHeader.fontSize = 11;
                        doc.styles.title = { fontSize: 16, bold: true, alignment: 'center', margin: [0, 0, 0, 10] };

                        // --- 2. Encabezado del Documento ---
                        const fechaGeneracion = new Date().toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
                        doc.content[0].text = `Reporte de Movimientos\nProducto: ${productoNombre}`;
                        doc.content.splice(1, 0, {
                            text: `Generado el: ${fechaGeneracion}`,
                            alignment: 'center',
                            fontSize: 9,
                            margin: [0, 0, 0, 20] // Margen inferior
                        });

                        // --- 3. Estilo de la Tabla ---
                        const table = doc.content[2];
                        table.widths = ['25%', '25%', '15%', '15%', '20%']; // Ancho de columnas
                        table.layout = {
                            fillColor: function (rowIndex) {
                                if (rowIndex === 0) return '#343a40'; // Color de fondo para la cabecera
                                return (rowIndex % 2 === 0) ? '#f8f9fa' : null; // Colores alternos para filas
                            }
                        };

                        // Estilos para las celdas de la cabecera
                        table.table.body[0].forEach(cell => {
                            cell.fillColor = '#343a40';
                            cell.color = 'white';
                            cell.bold = true;
                        });

                        // --- 4. Pie de Página con Numeración ---
                        doc.footer = function(currentPage, pageCount) {
                            return {
                                text: `Página ${currentPage.toString()} de ${pageCount}`,
                                alignment: 'center',
                                fontSize: 9,
                                margin: [0, 20, 0, 0]
                            };
                        };
                    }
                }
            ],
            columns: [
                { data: 'fechaVenta' },
                { data: 'numeroDocumento' },
                { data: 'precioVenta', className: 'text-end', render: (d) => `S/ ${parseFloat(d).toFixed(2)}` },
                { data: 'cantidad', className: 'text-center' },
                { data: 'subtotal', className: 'text-end', render: (d) => `S/ ${parseFloat(d).toFixed(2)}` }
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" },
            searching: false, // Opcional: deshabilita la búsqueda en la tabla modal
            paging: false, // Opcional: deshabilita la paginación
            info: false // Opcional: deshabilita el texto "Mostrando X de Y"
        });

        $('#totalVendidoProducto').text(`S/ ${parseFloat(data.totalVendido).toFixed(2)}`);
    }
});