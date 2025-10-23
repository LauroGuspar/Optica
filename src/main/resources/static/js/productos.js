$(document).ready(function () {
    const formId = '#formProducto';
    let productoModalEl = document.getElementById('productoModal');
    let productoModal = new bootstrap.Modal(productoModalEl);
    let dataTable;
    let isEditing = false;

    const API_BASE = '/productos/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        categorias: `${API_BASE}/categorias`,
        marcas: `${API_BASE}/marcas`,
        unidades: `${API_BASE}/unidades`
    };

    function initializeDataTable() {
        dataTable = $('#tablaProductos').DataTable({
            responsive: true,
            processing: true,
            ajax: { url: ENDPOINTS.list, dataSrc: 'data' },
            columns: [
                {
                    data: 'imagen',
                    orderable: false,
                    render: function (data) {
                        const firstImage = data ? data.split(',')[0].trim() : null;
                        const imageUrl = firstImage ? `/productos/${firstImage}` : '/images/placeholder.png';
                        return `<img src="${imageUrl}" alt="Producto" class="img-thumbnail" width="50">`;
                    }
                },
                { data: 'nombre', title: 'Nombre' },
                { data: 'codigo', title: 'Código' },
                { data: 'modelo', title: 'Modelo' },
                { data: 'categoria.nombre', title: 'Categoría' },
                { data: 'marca.nombre', title: 'Marca' },
                { data: 'descripcion', title: 'Descripción' },
                { data: 'precio', title: 'Precio', render: data => `S/ ${parseFloat(data || 0).toFixed(2)}` },
                { data: 'stock', title: 'Stock' },
                { data: 'stockMinimo', title: 'Stock Mín.' },
                { data: 'unidad.nombre', title: 'Unidad' },
                { data: 'fechaCreacion', title: 'Fec. Creación' },
                { data: 'fechaVencimiento', title: 'Fec. Venc.' },
                {
                    data: 'estado', title: 'Estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null, title: 'Acciones',
                    orderable: false, searchable: false,
                    render: (data, type, row) => AppUtils.createActionButtons(row)
                }
            ],
            columnDefs: [
                { targets: [3, 5, 6, 9, 10, 11, 12], visible: false }
            ],

            language: {
                "processing": "Procesando...",
                "lengthMenu": "Mostrar _MENU_ registros",
                "zeroRecords": "No se encontraron resultados",
                "emptyTable": "Ningún dato disponible en esta tabla",
                "info": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
                "infoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
                "infoFiltered": "(filtrado de un total de _MAX_ registros)",
                "search": "Buscar:",
                "loadingRecords": "Cargando...",
                "paginate": {
                    "first": "Primero",
                    "last": "Último",
                    "next": "Siguiente",
                    "previous": "Anterior"
                },
                "aria": {
                    "sortAscending": ": Activar para ordenar la columna de manera ascendente",
                    "sortDescending": ": Activar para ordenar la columna de manera descendente"
                }
            },

            dom: "<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
                "<'row'<'col-sm-12'tr>>" +
                "<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>" +
                "<'row'<'col-sm-12'B>>",
            buttons: [
                {
                    extend: 'colvis',
                    text: '👁️ Mostrar / Ocultar Columnas',
                    className: 'btn btn-outline-secondary'
                }
            ]
        });
    }

    function loadSelectOptions(endpoint, selector, placeholder) {
        fetch(endpoint)
            .then(response => response.json())
            .then(data => {
                const select = $(selector);
                select.empty().append(`<option value="" disabled selected>${placeholder}</option>`);
                data.forEach(item => {
                    select.append(`<option value="${item.id}">${item.nombre}</option>`);
                });
            });
    }

    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formId).on('submit', (e) => { e.preventDefault(); saveProducto(); });

        $('#tablaProductos tbody').on('click', '.action-edit', handleEdit);
        $('#tablaProductos tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaProductos tbody').on('click', '.action-delete', handleDelete);

        $('#imagenFiles').on('change', function () {
            if (this.files && this.files.length > 0) {
                $('#imagenActualGallery').hide();
                showMultipleImagePreviews(this, '#imagenPreviewGallery');
            } else {
                $('#imagenActualGallery').show();
                $('#imagenPreviewGallery').empty().hide();
            }
        });

        $('#imagenActualGallery').on('click', '.btn-delete-img', handleDeleteImage);
        productoModalEl.addEventListener('hidden.bs.modal', function () {
            if (document.activeElement) {
                document.activeElement.blur();
            }
        });
    }

    function saveProducto() {
        const producto = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val(),
            codigo: $('#codigo').val(),
            modelo: $('#modelo').val(),
            descripcion: $('#descripcion').val(),
            precio: $('#precio').val(),
            stock: $('#stock').val(),
            stockMinimo: $('#stockMinimo').val(),
            fechaVencimiento: $('#fechaVencimiento').val() || null,
            categoria: { id: $('#categoria').val() },
            marca: { id: $('#marca').val() },
            unidad: { id: $('#unidad').val() }
        };

        const formData = new FormData();
        const imagenFiles = $('#imagenFiles')[0].files;
        if (!imagenFiles || imagenFiles.length === 0) {
            producto.imagen = $('#imagenActual').val() || null;
        }

        formData.append('producto', JSON.stringify(producto));
        if (imagenFiles && imagenFiles.length > 0) {
            for (let i = 0; i < imagenFiles.length; i++) {
                formData.append('imagenFiles', imagenFiles[i]);
            }
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    productoModal.hide();
                    AppUtils.showNotification('Producto guardado correctamente', 'success');
                    dataTable.ajax.reload();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión.', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else {
                    AppUtils.showNotification('No se pudieron cargar los datos del producto', 'error');
                }
            })
            .catch(() => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleToggleStatus() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.toggleStatus(id), { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else { AppUtils.showNotification(data.message, 'error'); }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleDelete() {
        const id = $(this).data('id');
        Swal.fire({
            title: '¿Estás seguro?', text: "¡El proveedor será marcado como eliminado!",
            icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d', confirmButtonText: 'Sí, ¡eliminar!', cancelButtonText: 'Cancelar'
        }).then(result => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.delete(id), { method: 'DELETE' })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            AppUtils.showNotification(data.message, 'success');
                            reloadTable();
                        } else { AppUtils.showNotification(data.message, 'error'); }
                    })
                    .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
                    .finally(() => AppUtils.showLoading(false));
            }
        });
    }

    function openModalForNew() {
        isEditing = false;
        AppUtils.clearForm(formId);
        $('#modalTitle').text('Nuevo Producto');
        $('#imagenFiles').val('');
        $('#imagenActual').val('');
        $('#imagenPreviewGallery').empty().hide();
        $('#imagenActualGallery').empty().show().append('<p class="text-muted col-12">No hay imágenes para este nuevo producto.</p>');

        productoModal.show();
    }

    function openModalForEdit(producto) {
        isEditing = true;
        AppUtils.clearForm(formId);
        $('#modalTitle').text('Editar Producto');
        $('#id').val(producto.id);
        $('#nombre').val(producto.nombre);
        $('#codigo').val(producto.codigo);
        $('#modelo').val(producto.modelo);
        $('#descripcion').val(producto.descripcion);
        $('#precio').val(producto.precio);
        $('#stock').val(producto.stock);
        $('#stockMinimo').val(producto.stockMinimo);
        $('#fechaVencimiento').val(producto.fechaVencimiento);
        $('#categoria').val(producto.categoria.id);
        $('#marca').val(producto.marca.id);
        $('#unidad').val(producto.unidad.id);
        $('#imagenActual').val(producto.imagen || '');
        $('#imagenFiles').val('');
        $('#imagenPreviewGallery').empty().hide();

        const gallery = $('#imagenActualGallery');
        gallery.empty().show();

        if (producto.imagen && producto.imagen.length > 0) {
            const imagenes = producto.imagen.split(',');
            imagenes.forEach(imagenNombre => {
                const imgName = imagenNombre.trim();
                if (imgName) {
                    const imageUrl = `/productos/${imgName}`;
                    const imgHtml = `
                        <div class="col-3 col-md-2 img-preview-wrapper" id="img-wrapper-${imgName.replace(/[^a-zA-Z0-9]/g, '_')}">
                            <img src="${imageUrl}" alt="${imgName}" class="actual-gallery-img">
                            <button type="button" class="btn-delete-img" data-image-name="${imgName}">&times;</button>
                        </div>
                    `;
                    gallery.append(imgHtml);
                }
            });
        } else {
            gallery.append('<p class="text-muted col-12">Este producto no tiene imágenes.</p>');
        }

        productoModal.show();
    }

    function showMultipleImagePreviews(input, previewContainerId) {
        const previewContainer = $(previewContainerId);

        previewContainer.empty();

        if (input.files && input.files.length > 0) {
            previewContainer.show();
            previewContainer.append('<label class="form-label col-12">Nuevas imágenes a subir:</label>');
            Array.from(input.files).forEach(file => {
                const reader = new FileReader();
                reader.onload = function (e) {
                    const imgHtml = `
                        <div class="col-3 col-md-2">
                            <img src="${e.target.result}" alt="Vista previa" class="preview-gallery-img">
                        </div>
                    `;
                    previewContainer.append(imgHtml);
                };
                reader.readAsDataURL(file);
            });
        } else {
            previewContainer.hide();
        }
    }

    function handleDeleteImage() {
        const imgName = $(this).data('image-name');
        $(this).closest('.img-preview-wrapper').remove();
        const currentImagesStr = $('#imagenActual').val();
        const images = currentImagesStr.split(',');
        const newImages = images.filter(img => img.trim() !== imgName.trim());
        $('#imagenActual').val(newImages.join(','));
        if ($('#imagenActualGallery .img-preview-wrapper').length === 0) {
            $('#imagenActualGallery').append('<p class="text-muted col-12">No hay imágenes actuales.</p>');
        }
    }

    initializeDataTable();
    setupEventListeners();
    loadSelectOptions(`${API_BASE}/categorias`, '#categoria', 'Seleccione una Categoría');
    loadSelectOptions(`${API_BASE}/marcas`, '#marca', 'Seleccione una Marca');
    loadSelectOptions(`${API_BASE}/unidades`, '#unidad', 'Seleccione una Unidad');
});