$(document).ready(function() {
    let dataTable;
    let ventaModal;
    let isEditing = false;
    let productosEnVenta = []; // Array para manejar el "carrito"
    let gestionCuotasModal;
    let detalleVentaModal;
    let historialPagosModal;
    let clienteModal; // Modal para crear cliente
    let registrarPagoCuotaModal;
    let totalVentaActual = 0; // Variable para almacenar el total

    // Objeto para centralizar las URLs de la API, facilitando su mantenimiento.
    const API_BASE = '/ventas/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        get: (id) => `${API_BASE}/${id}`, // Endpoint para obtener una venta
        save: `${API_BASE}/guardar`,
        detail: (id) => `${API_BASE}/detalle/${id}`,
        anular: (id) => `${API_BASE}/anular/${id}`,
        cuotas: (id) => `${API_BASE}/cuotas/${id}`,
        pagarCuota: `${API_BASE}/cuotas/pagar`,
        // Endpoints de cliente ahora son más específicos
        consultarCliente: (doc) => `/clientes/api/documento/${doc}`,
        guardarCliente: '/clientes/api/guardar',
        productos: '/productos/api/listar',
        tiposComprobante: `${API_BASE}/tipos-comprobante`, // Endpoint para guardar cliente
        siguienteNumero: (id) => `${API_BASE}/siguiente-numero/${id}`
    };

    // Llamadas iniciales para configurar la página.
    initializeDataTable();
    setupEventListeners();

    function initializeDataTable() {
        // Inicializa la DataTable para mostrar el listado de ventas.
        dataTable = $('#tablaVentas').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: ENDPOINTS.list,
                data: function (d) {
                    // Añadir parámetros de fecha al request de DataTables
                    d.fechaInicio = $('#filtroFechaInicio').val();
                    d.fechaFin = $('#filtroFechaFin').val();
                    return d;
                },
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                { data: 'numeroDocumento' },
                { data: 'cliente' },
                { data: 'usuario' },
                { data: 'fecha' },
                {
                    data: 'total',
                    render: (data) => `S/ ${parseFloat(data).toFixed(2)}`
                },
                {
                    data: 'formaPago',
                    render: (data) => {
                        const badgeClass = data === 'CREDITO' ? 'text-bg-warning' : 'text-bg-info';
                        return `<span class="badge ${badgeClass}">${data}</span>`;
                    }
                },
                {
                    data: 'deuda',
                    render: (data) => {
                        const deuda = parseFloat(data);
                        return deuda > 0 ? `<span class="text-danger fw-bold">S/ ${deuda.toFixed(2)}</span>` : `S/ 0.00`;
                    }
                },
                {
                    data: 'estado',
                    render: (data) => data === 1
                        ? '<span class="badge text-bg-success">Completada</span>'
                        : '<span class="badge text-bg-danger">Anulada</span>'
                },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        let buttons = `<button class="btn btn-secondary btn-sm action-edit" data-id="${row.id}" title="Editar Venta"><i class="fas fa-edit"></i></button>
                                     <button class="btn btn-info btn-sm action-detail" data-id="${row.id}" title="Ver Detalle"><i class="fas fa-eye"></i></button>`;

                        if (row.estado === 1) {
                            buttons += ` <button class="btn btn-danger btn-sm action-anular" data-id="${row.id}" title="Anular Venta"><i class="fas fa-times-circle"></i></button>`;
                            if (row.formaPago === 'CREDITO' && parseFloat(row.deuda) > 0) {
                                buttons += ` <button class="btn btn-warning btn-sm action-cuotas" data-id="${row.id}" title="Gestionar Cuotas"><i class="fas fa-hand-holding-usd"></i></button>`;
                            }
                            if (row.formaPago === 'CREDITO') {
                                buttons += ` <button class="btn btn-primary btn-sm action-historial" data-id="${row.id}" title="Historial de Pagos"><i class="fas fa-history"></i></button>`;
                            }
                        }
                        return `<div class="btn-group" role="group">${buttons}</div>`;
                    }
                }
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" },
            order: [[0, 'desc']]
        });
    }

    function setupEventListeners() {
        // Asigna los manejadores de eventos a los diferentes elementos del DOM.
        $('#btnNuevaVenta').on('click', openModalForNew);
        $('#tablaVentas tbody').on('click', '.action-edit', handleEditVenta);
        $('#tablaVentas tbody').on('click', '.action-anular', handleAnularVenta);
        $('#tablaVentas tbody').on('click', '.action-detail', handleShowDetail);
        $('#tablaVentas tbody').on('click', '.action-cuotas', handleShowCuotas);
        $('#tablaVentas tbody').on('click', '.action-historial', handleShowHistorial);
        $('#btnGuardarVenta').on('click', saveVenta);
        $('#btnFiltrar').on('click', () => dataTable.ajax.reload());
        $('#btnLimpiarFiltros').on('click', handleClearFilters);

        // Al cerrar el modal, limpiar todo
        $('#modalVenta').on('hidden.bs.modal', function () {
            clearVentaForm();
        });

        // Delegación de eventos para la tabla de productos de la venta
        $('#tablaProductosVenta tbody').on('change', '.cantidad-producto', handleQuantityChange);

        // Delegación de eventos para pagar cuota
        $('#tablaGestionCuotas').on('click', '.btn-pagar-cuota', handlePagarCuota);
        $('#tablaProductosVenta tbody').on('click', '.btn-remover-producto', handleRemoveProduct);

        // Añadir delegación de eventos para el nuevo botón de pagar en el historial
        $('#tablaHistorialPagos').on('click', '.btn-pagar-cuota-historial', handlePagarCuota);


        // Listener para el botón de confirmar pago en el modal de registro de pago
        $('#btnConfirmarPagoCuota').on('click', confirmarPagoCuota);

        // Listeners para los nuevos campos de pago
        $('#esVentaCredito').on('change', toggleCamposCredito);
        $('#formaPago').on('change', handleFormaPagoChange); // Nuevo listener
        $('#pagoInicial').on('input', calcularDeuda);
        $('#cuotas, #intervaloCuotas').on('change', generarCamposFechaCuotas);
        $('#serieComprobante').on('change', cargarSiguienteNumero);

        // Listener para el nuevo botón de búsqueda de cliente
        $('#btnConsultarCliente').on('click', handleConsultarCliente);

    }

    function handleClearFilters() {
        $('#filtroFechaInicio').val('');
        $('#filtroFechaFin').val('');
        dataTable.ajax.reload();
    }


    function openModalForNew() {
        // Prepara y abre el modal para registrar una nueva venta.
        isEditing = false;
        clearVentaForm(); // Asegurarse de que el formulario esté limpio
        const modalElement = document.getElementById('modalVenta'); // Busca el elemento del modal
        if (modalElement) {
            // Siempre crea una nueva instancia o recupera la existente
            ventaModal = bootstrap.Modal.getOrCreateInstance(modalElement);
            cargarSeriesComprobante(null); // No hay venta existente
            initializeSelect2();
            ventaModal.show();
        } else {
            // Si el elemento no existe en el DOM, muestra un error claro.
            console.error("El elemento del modal con ID 'modalVenta' no fue encontrado en el DOM.");
            AppUtils.showNotification('Error: No se pudo encontrar el modal de venta.', 'error');
        }
    }

    function handleEditVenta() {
        // Obtiene los datos de una venta existente y abre el modal para editarla.
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.detail(id)) // Reutilizamos el endpoint de detalle
            .then(response => {
                if (!response.ok) throw new Error('No se pudo cargar la venta para editar.');
                return response.json();
            })
            .then(data => {
                openModalForEdit(data);
            })
            .catch(error => AppUtils.showNotification(error.message, 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function openModalForEdit(ventaData) {
        // Llena el formulario del modal con los datos de una venta para su edición.
        isEditing = true;
        clearVentaForm();
        $('#modalVentaLabel').text(`Editar Venta N° ${ventaData.numeroDocumento || ventaData.id}`);
        $('#ventaId').val(ventaData.id);

        // Llenar datos del cliente
        $('#ventaClienteId').val(ventaData.idCliente);
        $('#ventaClienteDocumento').val(ventaData.clienteDocumento).prop('disabled', true); // Deshabilitar en edición
        $('#ventaClienteNombre').val(ventaData.cliente);

        // Cargar productos
        productosEnVenta = ventaData.detalle.map(item => ({
            id: item.productoId,
            nombre: item.productoNombre,
            precio: item.precio,
            stockMax: item.stock, // El DTO de detalle ahora debe devolver el stock actual del producto
            cantidad: item.cantidad
        }));
        renderTablaProductosVenta();

        // Si la venta es a crédito, mostrar y llenar los campos correspondientes
        if (ventaData.formaPago === 'CREDITO') {
            $('#esVentaCredito').prop('checked', true).trigger('change'); // Activa el switch y los campos
            $('#pagoInicial').val(ventaData.pagoInicial || 0);
            $('#cuotas').val(ventaData.cuotas || 1);

            // Esperar un momento para que se generen los campos de fecha antes de llenarlos
            setTimeout(() => {
                if (ventaData.fechasCuotas && ventaData.fechasCuotas.length > 0) {
                    $('#contenedorFechasCuotas .fecha-cuota').each(function(index) {
                        if (ventaData.fechasCuotas[index]) {
                            $(this).val(ventaData.fechasCuotas[index]);
                        }
                    });
                }
            }, 100); // 100ms de espera
        }

        // Abre el modal directamente sin llamar a openModalForNew
        const modalElement = document.getElementById('modalVenta');
        if (modalElement) {
            ventaModal = bootstrap.Modal.getOrCreateInstance(modalElement);
            cargarSeriesComprobante(ventaData.idTipoComprobante); // Cargar y seleccionar la serie
            ventaModal.show();
        }
    }

    function cargarSeriesComprobante(idTipoSeleccionado) {
        // Obtiene las series de comprobante (Boleta, Factura) desde el backend y las carga en el select.
        fetch(ENDPOINTS.tiposComprobante)
            .then(response => response.json())
            .then(data => {
                const select = $('#serieComprobante');
                select.empty();
                if (data.length > 0) {
                    data.forEach(tipo => {
                        const option = new Option(`${tipo.nombre} (${tipo.serie})`, tipo.id);
                        select.append(option);
                    });
                    if (idTipoSeleccionado) { // Corregir el paréntesis mal ubicado
                        select.val(idTipoSeleccionado);
                    }
                    select.trigger('change'); // Dispara el evento para cargar el primer número
                } else {
                    select.append('<option value="">No hay series disponibles</option>');
                }
            })
            .catch(error => console.error('Error al cargar series:', error));
    }

    function cargarSiguienteNumero() {
        // Obtiene el siguiente número de documento correlativo para la serie seleccionada.
        const idTipo = $(this).val();
        if (idTipo) {
            fetch(ENDPOINTS.siguienteNumero(idTipo))
                .then(response => response.json())
                .then(data => {
                    $('#numeroVenta').val(data.siguienteNumero);
                })
                .catch(error => console.error('Error al cargar siguiente número:', error));
        }
    }

    function initializeSelect2() {
        // Inicializa el componente Select2 para la búsqueda de productos.
        // Select2 para Productos
        $('#selectProducto').select2({
            theme: 'bootstrap-5',
            dropdownParent: $('#modalVenta'),
            placeholder: 'Busque y agregue un producto',
            ajax: {
                url: ENDPOINTS.productos,
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    return { term: params.term }; // Envía el término de búsqueda
                },
                processResults: function (data) {
                    // Filtrar productos con estado activo y stock > 0
                    const productos = Array.isArray(data.data) ? data.data : [];
                    const productosActivos = productos.filter(p => p.estado === 1 && p.stock > 0);
                    return {
                        results: productosActivos.map(producto => ({
                            id: producto.id,
                            text: `${producto.nombre} (Stock: ${producto.stock})`,
                            // Pasar todo el objeto producto
                            producto: producto
                        }))
                    };
                },
                cache: true
            }
        }).on('select2:select', function (e) {
            const data = e.params.data;
            agregarProductoAVenta(data.producto);
            // Limpiar el select para la siguiente búsqueda
            $(this).val(null).trigger('change');
        });
    }

    function agregarProductoAVenta(producto) {
        // Añade un producto al "carrito" de la venta o incrementa su cantidad si ya existe.
        // Verificar si el producto ya está en el carrito
        const productoExistente = productosEnVenta.find(p => p.id === producto.id);

        if (productoExistente) {
            // Si ya existe, solo aumentar la cantidad si hay stock
            if (productoExistente.cantidad < producto.stock) {
                productoExistente.cantidad++;
            } else {
                AppUtils.showNotification('No hay más stock disponible para este producto.', 'warning');
            }
        } else {
            // Si es nuevo, agregarlo con cantidad 1
            productosEnVenta.push({
                id: producto.id,
                nombre: producto.nombre,
                precio: producto.precio,
                stockMax: producto.stock,
                cantidad: 1
            });
        }
        renderTablaProductosVenta();
    }

    function renderTablaProductosVenta() {
        // Dibuja la tabla de productos del "carrito" y calcula el total de la venta.
        const tbody = $('#tablaProductosVenta tbody');
        tbody.empty();
        totalVentaActual = 0;

        if (productosEnVenta.length === 0) {
            tbody.html('<tr><td colspan="6" class="text-center">Aún no hay productos en la venta.</td></tr>');
        } else {
            productosEnVenta.forEach((p, index) => {
                const subtotal = p.precio * p.cantidad;
                totalVentaActual += subtotal;
                const fila = `
                    <tr>
                        <td>${p.nombre}</td>
                        <td>S/ ${parseFloat(p.precio).toFixed(2)}</td>
                        <td class="text-center">${p.stockMax}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm cantidad-producto" 
                                   value="${p.cantidad}" min="1" max="${p.stockMax}" data-index="${index}">
                        </td>
                        <td>S/ ${subtotal.toFixed(2)}</td>
                        <td>
                            <button class="btn btn-danger btn-sm btn-remover-producto" data-index="${index}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
                tbody.append(fila);
            });
        }

        $('#totalVenta').text(`S/ ${totalVentaActual.toFixed(2)}`);
        calcularDeuda(); // Recalcular deuda si el total cambia
    }

    function saveVenta() {
        // Recolecta todos los datos del formulario y los envía al backend para guardar la venta.
        const idVenta = $('#ventaId').val();
        const idCliente = $('#ventaClienteId').val();
        const idTipoComprobante = $('#serieComprobante').val();
        const esCredito = $('#esVentaCredito').is(':checked');
        if (!idCliente) {
            AppUtils.showNotification('Debe seleccionar un cliente.', 'error');
            return;
        }
        if (productosEnVenta.length === 0) {
            AppUtils.showNotification('Debe agregar al menos un producto a la venta.', 'error');
            return;
        }

        const ventaData = {
            id: idVenta ? parseInt(idVenta) : null,
            idCliente: parseInt(idCliente),
            productos: productosEnVenta.map(p => ({ id: p.id, cantidad: p.cantidad })),
            idTipoComprobante: parseInt(idTipoComprobante),
            formaPago: esCredito ? 'CREDITO' : 'CONTADO',
            medioPago: $('#medioPago').val()
        };

        if (esCredito) {
            const pagoInicial = parseFloat($('#pagoInicial').val()) || 0;
            // Recolectar fechas de cuotas
            const fechasCuotas = [];
            let todasLasFechasSonValidas = true;
            $('#contenedorFechasCuotas .fecha-cuota').each(function() {
                const fecha = $(this).val();
                if (!fecha) {
                    todasLasFechasSonValidas = false;
                }
                fechasCuotas.push(fecha);
            });

            if (fechasCuotas.length > 0 && !todasLasFechasSonValidas) {
                AppUtils.showNotification('Debe programar la fecha para todas las cuotas.', 'error');
                return;
            }

            ventaData.pagoInicial = pagoInicial;
            ventaData.cuotas = parseInt($('#cuotas').val()) || 1;
            ventaData.fechasCuotas = fechasCuotas;
            ventaData.deuda = totalVentaActual - pagoInicial;
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ventaData)
        })
            .then(response => {
                if (response.ok) {
                    ventaModal.hide();
                    AppUtils.showNotification(isEditing ? 'Venta actualizada' : 'Venta registrada', 'success');
                    dataTable.ajax.reload(null, false);
                } else {
                    return response.text().then(text => { throw new Error(text) });
                }
            })
            .catch(error => {
                AppUtils.showNotification(error.message || 'Error al guardar la venta.', 'error');
            })
            .finally(() => AppUtils.showLoading(false));
    }

    function handleAnularVenta() {
        // Muestra un diálogo de confirmación y, si se confirma, anula la venta.
        const id = $(this).data('id');
        AppUtils.showConfirmationDialog({
            title: '¿Anular esta venta?',
            text: 'Esta acción devolverá los productos al stock. Esta acción no se puede deshacer.'
        }, () => {
            AppUtils.showLoading(true);
            fetch(ENDPOINTS.anular(id), { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        AppUtils.showNotification('Venta anulada.', 'success');
                        dataTable.ajax.reload(null, false);
                    } else {
                        AppUtils.showNotification('Error al anular la venta.', 'error');
                    }
                })
                .catch(error => AppUtils.showNotification('Error de conexión.', 'error'))
                .finally(() => AppUtils.showLoading(false));
        });
    }

    function handleConsultarCliente() {
        // Consulta la API externa para obtener datos de un cliente por DNI/RUC.
        const documento = $('#ventaClienteDocumento').val().trim();
        if (documento.length !== 8 && documento.length !== 11) {
            AppUtils.showNotification('El DNI debe tener 8 dígitos y el RUC 11.', 'warning');
            return;
        }

        const btn = $('#btnConsultarCliente');
        const originalIcon = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>').prop('disabled', true);

        fetch(ENDPOINTS.consultarCliente(documento))
            .then(response => response.json())
            .then(async data => {
                console.log(data);
                if (data.error) {
                    AppUtils.showNotification(data.error, 'error');
                    return;
                }

                data = data?.data;

                // Si el cliente ya existe en la BD (devuelve 'id'), lo usamos.
                if (data && data.id) {
                    $('#ventaClienteId').val(data.id);
                    $('#ventaClienteNombre').val(data.nombre + ' ' + data.apellidoPaterno + ' ' + data.apellidoMaterno);
                    AppUtils.showNotification('Cliente encontrado en la base de datos.', 'success');
                } else if (documento.length === 8) {
                    const response = await fetch(`/reniec/api/buscar-dni/${documento}`);
                    const result = await response.json();

                    if (result.success && result.data && result.data.datos) {
                        const datos = result.data.datos;

                        const direccion = () => {
                            if (!datos.domiciliado) return null;
                            const d = datos.domiciliado;
                            return [d.direccion, d.distrito, d.provincia, d.departamento]
                                .filter(Boolean)
                                .join(', ');
                        }

                        const nuevoCliente = {
                            id: null,
                            nombre: datos.nombres || "",
                            apellidoPaterno: datos.ape_paterno,
                            apellidoMaterno: datos.ape_materno,
                            correo: null,
                            telefono: null,
                            direccion: direccion(),
                            ndocumento: documento,
                            tipodocumento: { id: 1 },
                        };

                        return fetch(ENDPOINTS.guardarCliente, {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(nuevoCliente)
                        })
                            .then(res => res.json())
                            .then(clienteGuardado => {
                                console.log(clienteGuardado);
                                $('#ventaClienteId').val(clienteGuardado.cliente.id);
                                $('#ventaClienteNombre').val(clienteGuardado.cliente.nombre + ' ' + clienteGuardado.cliente.apellidoPaterno + ' ' + clienteGuardado.cliente.apellidoPaterno);
                                AppUtils.showNotification('Nuevo cliente creado y seleccionado.', 'success');
                            });
                    } else {
                        Swal.fire('No encontrado', result.message || 'No se encontraron datos para este RUC', 'info');
                    }
                } else if (documento.length === 11) {
                    const response = await fetch(`/reniec/api/buscar-ruc/${documento}`);
                    const result = await response.json();

                    if (result.success && result.data && result.data.datos) {
                        const datos = result.data.datos;

                        const direccion = () => {
                            if (!datos.domiciliado) return null;
                            const d = datos.domiciliado;
                            return [d.direccion, d.distrito, d.provincia, d.departamento]
                                .filter(Boolean)
                                .join(', ');
                        }

                        const nuevoCliente = {
                            id: null,
                            nombre: datos.razon_social || "",
                            apellidoPaterno: null,
                            apellidoMaterno: null,
                            correo: null,
                            telefono: null,
                            direccion: direccion(),
                            ndocumento: documento,
                            tipodocumento: { id: 2 },
                        };

                        return fetch(ENDPOINTS.guardarCliente, {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(nuevoCliente)
                        })
                            .then(res => res.json())
                            .then(clienteGuardado => {
                                $('#ventaClienteId').val(clienteGuardado.cliente.id);
                                $('#ventaClienteNombre').val(clienteGuardado.cliente.nombre);
                                AppUtils.showNotification('Nuevo cliente creado y seleccionado.', 'success');
                            });
                    } else {
                        Swal.fire('No encontrado', result.message || 'No se encontraron datos para este DNI', 'info');
                    }
                } else {
                    AppUtils.showNotification('No se encontró un nombre para el documento.', 'warning');
                }
            })
            .catch(error => AppUtils.showNotification('Error al consultar o crear el cliente.' + error, 'error'))
            .finally(() => {
                btn.html(originalIcon).prop('disabled', false);
            });
    }

    function handleShowDetail() {
        // Obtiene y muestra el detalle completo de una venta en un modal.
        const id = $(this).data('id');
        AppUtils.showLoading(true);

        fetch(ENDPOINTS.detail(id))
            .then(response => {
                if (!response.ok) throw new Error('No se pudo cargar el detalle.');
                return response.json();
            })
            .then(data => {
                // Inicializar el modal de detalle solo cuando se va a usar
                if (!detalleVentaModal) {
                    const modalElement = document.getElementById('modalDetalleVenta');
                    if (modalElement) {
                        detalleVentaModal = new bootstrap.Modal(modalElement);
                    }
                }
                populateDetailModal(data);
                detalleVentaModal.show();
            })
            .catch(error => AppUtils.showNotification(error.message, 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function populateDetailModal(data) {
        // Llena el modal de detalle con la información de la venta.
        const infoContainer = $('#detalleVentaInfo');
        const productosTbody = $('#tablaDetalleProductos');
        const totalSpan = $('#detalleVentaTotal');

        infoContainer.html(`
            <div class="row">
                <div class="col-md-6">
                    <p><strong>ID Venta:</strong> ${data.id}</p>
                    <p><strong>Cliente:</strong> ${data.cliente} (${data.clienteDocumento || 'N/A'})</p>
                </div>
                <div class="col-md-6">
                    <p><strong>Fecha:</strong> ${data.fecha}</p>
                    <p><strong>Vendedor:</strong> ${data.usuario}</p>
                    <p><strong>Comprobante:</strong> ${data.numeroDocumento}</p>
                </div>
            </div>
        `);

        productosTbody.empty();
        data.detalle.forEach(item => {
            const fila = `
                <tr>
                    <td>${item.productoNombre}</td>
                    <td class="text-end">S/ ${parseFloat(item.precio).toFixed(2)}</td>
                    <td class="text-center">${item.cantidad}</td>
                    <td class="text-end">S/ ${parseFloat(item.subtotal).toFixed(2)}</td>
                </tr>
            `;
            productosTbody.append(fila);
        });

        totalSpan.text(`S/ ${parseFloat(data.total).toFixed(2)}`);
    }

    function handleShowCuotas() {
        // Obtiene y muestra el plan de pagos (cuotas) de una venta a crédito.
        const id = $(this).data('id');
        AppUtils.showLoading(true);

        fetch(ENDPOINTS.cuotas(id))
            .then(response => {
                if (!response.ok) throw new Error('No se pudo cargar el plan de pagos.');
                return response.json();
            })
            .then(data => {
                if (!gestionCuotasModal) {
                    const modalElement = document.getElementById('modalGestionCuotas');
                    if (modalElement) {
                        gestionCuotasModal = new bootstrap.Modal(modalElement);
                    }
                }
                populateCuotasModal(data, id);
                gestionCuotasModal.show();
            })
            .catch(error => AppUtils.showNotification(error.message, 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function populateCuotasModal(data, ventaId) {
        // Llena el modal de gestión de cuotas con los datos del plan de pagos.
        const infoContainer = $('#cuotasVentaInfo');
        const cuotasTbody = $('#tablaGestionCuotas');
        const deudaSpan = $('#cuotasDeudaTotal');

        infoContainer.html(`
            <div class="row">
                <div class="col-md-6"><p><strong>ID Venta:</strong> ${ventaId}</p></div>
                <div class="col-md-6"><p><strong>Cliente:</strong> ${data.clienteNombre}</p></div>
            </div>
        `);

        cuotasTbody.empty();
        data.cuotas.forEach(cuota => {
            const estadoBadge = cuota.estado === 'PAGADO'
                ? '<span class="badge text-bg-success">Pagado</span>'
                : '<span class="badge text-bg-warning">Pendiente</span>';

            const actionButton = cuota.estado === 'PENDIENTE'
                ? `<button class="btn btn-success btn-sm btn-pagar-cuota" data-venta-id="${ventaId}" data-cuota-numero="${cuota.numero}">Pagar</button>`
                : '';

            const fila = `
                <tr>
                    <td class="text-center">${cuota.numero}</td>
                    <td>${cuota.fecha || 'N/A'}</td>
                    <td class="text-end">S/ ${parseFloat(cuota.monto).toFixed(2)}</td>
                    <td class="text-center">${estadoBadge}</td>
                    <td class="text-center">${actionButton}</td>
                </tr>
            `;
            cuotasTbody.append(fila);
        });

        deudaSpan.text(`S/ ${parseFloat(data.deudaActual).toFixed(2)}`);
    }

    function handlePagarCuota() {
        // Abre un modal para registrar el pago de una cuota específica.
        const ventaId = $(this).data('venta-id');
        const numeroCuota = $(this).data('cuota-numero');

        // Abrir el modal para solicitar el medio de pago
        if (!registrarPagoCuotaModal) {
            registrarPagoCuotaModal = new bootstrap.Modal(document.getElementById('modalRegistrarPagoCuota'));
        }

        // Guardar los datos en el botón del modal para usarlos después
        $('#numeroCuotaAPagar').text(numeroCuota);
        const btnConfirmar = $('#btnConfirmarPagoCuota');
        btnConfirmar.data('venta-id', ventaId);
        btnConfirmar.data('cuota-numero', numeroCuota);

        registrarPagoCuotaModal.show();
    }

    function confirmarPagoCuota() {
        // Confirma y envía la petición para registrar el pago de una cuota.
        const btn = $(this);
        const ventaId = btn.data('venta-id');
        const numeroCuota = btn.data('cuota-numero');
        const medioPago = $('#medioPagoCuota').val();

        if (!medioPago) {
            AppUtils.showNotification('Debe seleccionar un medio de pago.', 'error');
            return;
        }

        registrarPagoCuotaModal.hide(); // Ocultar el modal de registro

        AppUtils.showConfirmationDialog({
            title: '¿Registrar Pago?',
            text: `Se registrará el pago para la cuota N° ${numeroCuota} con ${medioPago}.`
        }, () => {
            AppUtils.showLoading(true);
            fetch(ENDPOINTS.pagarCuota, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ventaId: ventaId, numeroCuota: numeroCuota, medioPago: medioPago })
            })
                .then(response => {
                    if (!response.ok) throw new Error('Error al registrar el pago.');
                    AppUtils.showNotification('Pago registrado exitosamente.', 'success');
                    dataTable.ajax.reload(null, false); // Recargar tabla principal
                    if (gestionCuotasModal) {
                        gestionCuotasModal.hide(); // Ocultar modal de gestión si está abierto
                    }
                })
                .catch(error => AppUtils.showNotification(error.message, 'error'))
                .finally(() => AppUtils.showLoading(false));
        });
    }

    function handleShowHistorial() {
        // Obtiene y muestra el historial de pagos de una venta a crédito.
        const id = $(this).data('id');
        AppUtils.showLoading(true);

        fetch(ENDPOINTS.cuotas(id)) // Reutilizamos el endpoint de cuotas
            .then(response => {
                if (!response.ok) throw new Error('No se pudo cargar el historial de pagos.');
                return response.json();
            })
            .then(data => {
                if (!historialPagosModal) {
                    historialPagosModal = new bootstrap.Modal(document.getElementById('modalHistorialPagos'));
                }
                populateHistorialModal(data, id);
                historialPagosModal.show();
            })
            .catch(error => AppUtils.showNotification(error.message, 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function populateHistorialModal(data, ventaId) {
        // Llena el modal de historial con los datos de las cuotas y pagos.
        const infoContainer = $('#historialVentaInfo');
        const historialTbody = $('#tablaHistorialPagos');
        const infoLiquidacion = $('#infoLiquidacion');

        infoContainer.html(`
            <div class="row">
                <div class="col-md-6"><p><strong>ID Venta:</strong> ${ventaId}</p></div>
                <div class="col-md-6"><p><strong>Cliente:</strong> ${data.clienteNombre}</p></div>
            </div>
        `);

        historialTbody.empty();
        const pagosRealizados = data.cuotas.filter(c => c.estado === 'PAGADO');

        if (data.cuotas.length === 0) {
            historialTbody.html('<tr><td colspan="5" class="text-center">Esta venta a crédito no tiene un plan de cuotas programado.</td></tr>');
        } else {
            data.cuotas.forEach(cuota => {
                const estadoBadge = cuota.estado === 'PAGADO'
                    ? `<span class="badge text-bg-success">Pagado el ${new Date(cuota.fechaPago).toLocaleDateString('es-PE')}</span>`
                    : `<span class="badge text-bg-warning">Pendiente</span>`;

                const actionButton = cuota.estado === 'PENDIENTE'
                    ? `<button class="btn btn-success btn-sm btn-pagar-cuota-historial" data-venta-id="${ventaId}" data-cuota-numero="${cuota.numero}">Pagar</button>`
                    : (cuota.medioPago || 'N/A');

                const fila = `
                    <tr>
                        <td class="text-center">${cuota.numero}</td>
                        <td>${cuota.fecha}</td>
                        <td class="text-end">S/ ${parseFloat(cuota.monto).toFixed(2)}</td>
                        <td class="text-center">${estadoBadge}</td>
                        <td class="text-center">${actionButton}</td>
                    </tr>
                `;
                historialTbody.append(fila);
            });
        }

        // Mostrar deuda actual y mensaje de liquidación
        const deudaSpan = $('#historialDeudaTotal');
        deudaSpan.text(`S/ ${parseFloat(data.deudaActual).toFixed(2)}`);
        if (data.deudaActual <= 0) {
            deudaSpan.removeClass('text-danger').addClass('text-success');
            const ultimoPago = pagosRealizados.sort((a, b) => new Date(b.fechaPago) - new Date(a.fechaPago))[0];
            if (ultimoPago) {
                infoLiquidacion.html(`<strong>Deuda liquidada completamente.</strong> Fecha de cancelación: ${new Date(ultimoPago.fechaPago).toLocaleDateString('es-PE')}`).show();
            } else {
                infoLiquidacion.html(`<strong>Deuda liquidada completamente.</strong>`).show();
            }
        } else {
            deudaSpan.removeClass('text-success').addClass('text-danger');
            infoLiquidacion.hide();
        }
    }

    function clearVentaForm() {
        // Limpia y resetea todos los campos del formulario de venta.
        productosEnVenta = [];
        $('#ventaId').val('');
        $('#ventaClienteId').val('');
        $('#ventaClienteDocumento').val('').prop('disabled', false);
        $('#ventaClienteNombre').val('');
        $('#selectProducto').val(null).trigger('change');
        $('#serieComprobante').val($('#serieComprobante option:first').val()).trigger('change');
        $('#numeroVenta').val('');
        $('#formVenta')[0].reset();
        $('#esVentaCredito').prop('checked', false);
        $('#camposCredito').hide();
        $('#contenedorFechasCuotas').empty();
        renderTablaProductosVenta();
    }

    function toggleCamposCredito() {
        // Muestra u oculta los campos relacionados con la venta a crédito.
        const esCredito = $(this).is(':checked');
        const camposCredito = $('#camposCredito');

        // Sincronizar el dropdown de Forma de Pago
        $('#formaPago').val(esCredito ? 'CREDITO' : 'CONTADO');

        if (esCredito) {
            camposCredito.slideDown();
            generarCamposFechaCuotas();
        } else {
            camposCredito.slideUp();
            // Limpiar campos de crédito al ocultar
            $('#pagoInicial').val('');
            $('#cuotas').val('1');
            $('#contenedorFechasCuotas').empty(); // Limpiar campos de fecha de cuotas
        }
        calcularDeuda();
    }

    function handleFormaPagoChange() {
        // Sincroniza el interruptor de crédito cuando se cambia la forma de pago manualmente (si estuviera habilitado).
        const esCredito = $(this).val() === 'CREDITO';
        // Sincronizar el interruptor de crédito y disparar su evento 'change'
        $('#esVentaCredito').prop('checked', esCredito).trigger('change');
    }

    function generarCamposFechaCuotas() {
        // Genera dinámicamente los campos de fecha para cada cuota de una venta a crédito.
        const contenedor = $('#contenedorFechasCuotas');
        const numCuotas = parseInt($('#cuotas').val()) || 0;
        const intervalo = parseInt($('#intervaloCuotas').val()) || 15;
        const esCredito = $('#esVentaCredito').is(':checked');

        contenedor.empty();

        if (esCredito && numCuotas > 0) {
            contenedor.append('<h6 class="text-secondary col-12 mt-2">Fechas de Pago Programadas</h6>');
            let fechaActual = new Date();
            for (let i = 1; i <= numCuotas; i++) {
                fechaActual.setDate(fechaActual.getDate() + intervalo);
                const fechaFormateada = fechaActual.toISOString().split('T')[0];
                const campoFecha = `
                    <div class="col-md-4">
                        <label for="fechaCuota${i}" class="form-label">Fecha Cuota ${i}</label>
                        <input type="date" class="form-control form-control-sm fecha-cuota" id="fechaCuota${i}" value="${fechaFormateada}">
                    </div>
                `;
                contenedor.append(campoFecha);
            }
        }
    }

    function calcularDeuda() {
        // Calcula y muestra la deuda restante en una venta a crédito.
        const pagoInicial = parseFloat($('#pagoInicial').val()) || 0;
        const deuda = totalVentaActual - pagoInicial;
        const deudaFormateada = deuda > 0 ? deuda.toFixed(2) : '0.00';
        $('#deudaRestante').text(`S/ ${deudaFormateada}`);
    }

    // --- Handlers para eventos delegados ---

    function handleQuantityChange() {
        // Actualiza la cantidad de un producto en el carrito y recalcula el total.
        const index = $(this).data('index');
        let nuevaCantidad = parseInt($(this).val());
        const producto = productosEnVenta[index];

        if (nuevaCantidad > producto.stockMax) {
            nuevaCantidad = producto.stockMax;
            $(this).val(nuevaCantidad);
            AppUtils.showNotification('Cantidad excede el stock disponible.', 'warning');
        }
        if (nuevaCantidad < 1) {
            nuevaCantidad = 1;
            $(this).val(nuevaCantidad);
        }
        producto.cantidad = nuevaCantidad;
        renderTablaProductosVenta();
    }

    function handleRemoveProduct() {
        // Elimina un producto del carrito y recalcula el total.
        const index = $(this).data('index');
        productosEnVenta.splice(index, 1);
        renderTablaProductosVenta();
    }

});
