$(document).ready(function () {
    let isEditing = false;
    let dataTable;
    let proveedorModal;
    const formid = '#formProveedor';

    const API_BASE = '/proveedores/api';
    const RUC_API = '/reniec/api/buscar-ruc';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`
    };

    initializeDataTable();
    proveedorModal = new bootstrap.Modal(document.getElementById('proveedorModal'));
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaProveedores').DataTable({
            responsive: true,
            processing: true,
            ajax: { url: ENDPOINTS.list, dataSrc: 'data' },
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'ndocumento' },
                { data: 'telefono' },
                { data: 'correo' },
                {
                    data: 'estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: (data, type, row) => AppUtils.createActionButtons(row)
                }
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
            }
        });
    }

    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formid).on('submit', (e) => { e.preventDefault(); saveProveedor(); });
        $('#tablaProveedores tbody').on('click', '.action-edit', handleEdit);
        $('#tablaProveedores tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaProveedores tbody').on('click', '.action-delete', handleDelete);
        $('#btnBuscarRuc').on('click', buscarRuc);
    }

    function reloadTable() { dataTable.ajax.reload(); }

    function saveProveedor() {
        AppUtils.clearFormErrors(formid);

        const proveedorData = {
            id: $('#id').val() || null,
            ndocumento: $('#ndocumento').val().trim(),
            nombre: $('#nombre').val().trim(),
            nombreComercial: $('#nombreComercial').val().trim(),
            direccion: $('#direccion').val().trim(),
            nacionalidad: $('#nacionalidad').val().trim(),
            telefono: $('#telefono').val().trim(),
            correo: $('#correo').val().trim(),
            correoAdicional: $('#correoAdicional').val().trim() || null
        };

        if (!proveedorData.ndocumento || proveedorData.ndocumento.length !== 11) {
            AppUtils.showFieldError('ndocumento', 'El RUC debe tener 11 dígitos.'); return;
        }
        if (!proveedorData.nombre) {
            AppUtils.showFieldError('nombre', 'La Razón Social es obligatoria.'); return;
        }
        if (!proveedorData.correo) {
            AppUtils.showFieldError('correo', 'El Correo es obligatorio.'); return;
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(proveedorData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    proveedorModal.hide();
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else {
                    if (data.errors) {
                        Object.keys(data.errors).forEach(key => {
                            AppUtils.showFieldError(key, data.errors[key]);
                        });
                    } else {
                        AppUtils.showNotification(data.message, 'error');
                    }
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else { AppUtils.showNotification('Error al cargar proveedor', 'error'); }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
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
        }).then((result) => {
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
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Agregar Proveedor');
        $('#nacionalidad').val('Peruana');
        proveedorModal.show();
    }

    function openModalForEdit(proveedor) {
        isEditing = true;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Editar Proveedor');

        $('#id').val(proveedor.id);
        $('#ndocumento').val(proveedor.ndocumento);
        $('#nombre').val(proveedor.nombre);
        $('#nombreComercial').val(proveedor.nombreComercial);
        $('#direccion').val(proveedor.direccion);
        $('#nacionalidad').val(proveedor.nacionalidad);
        $('#telefono').val(proveedor.telefono);
        $('#correo').val(proveedor.correo);
        $('#correoAdicional').val(proveedor.correoAdicional);

        proveedorModal.show();
    }

    function buscarRuc() {
        const ruc = $('#ndocumento').val().trim();
        if (ruc.length !== 11) {
            AppUtils.showNotification('El RUC debe tener 11 dígitos', 'error');
            AppUtils.showFieldError('ndocumento', 'El RUC debe tener 11 dígitos.');
            return;
        }

        AppUtils.clearFormErrors(formid);
        AppUtils.showLoading(true);

        fetch(`${RUC_API}/${ruc}`)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data && data.data.success) {

                    const apiData = data.data.datos;
                    $('#nombre').val(apiData.razon_social || '');
                    $('#nombreComercial').val(apiData.razon_social || '');
                    $('#direccion').val(apiData.domiciliado.direccion || '');
                    $('#nacionalidad').val('Peruana');

                    AppUtils.showNotification('Datos de RUC cargados', 'success');
                } else {
                    AppUtils.showNotification(data.data.message || 'No se pudo encontrar el RUC', 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error al conectar con la API de RUC', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }
    if (typeof AppUtils.clearFormErrors === 'undefined') {
        AppUtils.clearFormErrors = function (formId) {
            $(`${formId} .form-control, ${formId} .form-select`).removeClass('is-invalid');
            $(`${formId} .invalid-feedback`).text('');
        }
    }
    if (typeof AppUtils.showFieldError === 'undefined') {
        AppUtils.showFieldError = function (fieldId, message) {
            $(`#${fieldId}`).addClass('is-invalid');
            $(`#${fieldId}-error`).text(message);
        }
    }

});