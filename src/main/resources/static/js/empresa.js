$(document).ready(function () {
    const formid = '#formEmpresa';
    const placeholderLogo = 'https://via.placeholder.com/300x150.png?text=Logo';

    const API_BASE = '/empresa/api';
    const ENDPOINTS = {
        get: `${API_BASE}/datos`,
        update: `${API_BASE}/actualizar`,
        deleteSidebarImg: (id) => `${API_BASE}/sidebar/eliminar/${id}`
    };

    loadEmpresaData();
    $(formid).on('submit', (e) => {
        e.preventDefault();
        saveEmpresa();
    });

    $('#sidebar-gallery').on('click', '.btn-delete-img', function () {
        const imageId = $(this).data('id');
        deleteSidebarImage(imageId);
    });

    $('#logoFile').on('change', function () {
        showImagePreview(this, '#logoPreview');
    });

    $('#sidebarFiles').on('change', function () {
        showMultipleImagePreviews(this);
    });

    function loadEmpresaData() {
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    const empresa = data.data;
                    $('#nombre').val(empresa.nombre);
                    $('#ruc').val(empresa.ruc);
                    $('#direccion').val(empresa.direccion);
                    const logoSrc = empresa.logo ? `/empresa-logos/${empresa.logo}` : placeholderLogo;
                    $('#logoPreview').attr('src', logoSrc);
                    const gallery = $('#sidebar-gallery');
                    const noImagesMsg = $('#no-sidebar-images');
                    gallery.empty();

                    if (empresa.sidebarImagenes && empresa.sidebarImagenes.length > 0) {
                        noImagesMsg.hide();
                        empresa.sidebarImagenes.forEach(imagen => {
                            const imgSrc = `/empresa-sidebars/${imagen.ruta}`;
                            const imgHtml = `
                                <div class="col-6 col-md-4" id="sidebar-img-${imagen.id}">
                                    <div class="sidebar-img-wrapper">
                                        <img src="${imgSrc}" alt="Imagen Sidebar">
                                        <button type="button" class="btn btn-danger btn-sm btn-delete-img" data-id="${imagen.id}" title="Eliminar">
                                            X
                                        </button>
                                    </div>
                                </div>
                            `;
                            gallery.append(imgHtml);
                        });
                    } else {
                        gallery.append('<div class="col-12"><p class="text-muted">No hay imágenes de sidebar.</p></div>');
                    }
                } else {
                    AppUtils.showNotification('No se pudieron cargar los datos de la empresa', 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function saveEmpresa() {
        const formData = new FormData();
        const logoFile = $('#logoFile')[0].files[0];
        const sidebarFiles = $('#sidebarFiles')[0].files;

        if (logoFile) {
            formData.append('logoFile', logoFile);
        }

        if (sidebarFiles && sidebarFiles.length > 0) {
            for (let i = 0; i < sidebarFiles.length; i++) {
                formData.append('sidebarFiles', sidebarFiles[i]);
            }
        }

        if (!logoFile && (!sidebarFiles || sidebarFiles.length === 0)) {
            AppUtils.showNotification('No se seleccionó ningún archivo nuevo para actualizar', 'success');
            return;
        }

        AppUtils.showLoading(true);

        fetch(ENDPOINTS.update, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    AppUtils.showNotification(data.message, 'success');
                    AppUtils.clearForm(formid);
                    $('#sidebar-preview-gallery').hide().find('.col-6').remove();
                    loadEmpresaData();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function deleteSidebarImage(id) {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "¡La imagen será eliminada permanentemente!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, ¡eliminar!',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.deleteSidebarImg(id), { method: 'DELETE' })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            AppUtils.showNotification(data.message, 'success');
                            // Eliminar la imagen del DOM sin recargar toda la página
                            $(`#sidebar-img-${id}`).remove();
                            if ($('#sidebar-gallery .col-6').length === 0) {
                                $('#sidebar-gallery').append('<div class="col-12"><p class="text-muted">No hay imágenes de sidebar.</p></div>');
                            }
                        } else {
                            AppUtils.showNotification(data.message, 'error');
                        }
                    })
                    .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
                    .finally(() => AppUtils.showLoading(false));
            }
        });
    }

    function showImagePreview(input, previewId) {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $(previewId).attr('src', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function showMultipleImagePreviews(input) {
        const previewContainer = $('#sidebar-preview-gallery');
        previewContainer.children(':not(label)').remove();

        if (input.files && input.files.length > 0) {
            previewContainer.show();
            Array.from(input.files).forEach(file => {
                const reader = new FileReader();

                reader.onload = function (e) {
                    const imgHtml = `
                        <div class="col-6 col-md-4">
                            <img src="${e.target.result}" alt="Vista previa" class="sidebar-preview-img">
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
});