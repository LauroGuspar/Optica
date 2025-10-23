-- No se olviden de crear la base de Datos antes de ejercutar esto, la BD se llama "optica"
-- create database optica


create table categoria(
id_categoria bigint auto_increment primary key,
categ_nombre varchar(100) not null unique,
categ_estado int not null default 1
);

-- Volcado de Datos Cateogia
INSERT INTO `categoria` (`id_categoria`, `categ_nombre`, `categ_estado`) VALUES
(1, 'Niños', 1),
(2, 'Hombre', 1),
(3, 'Mujer', 1),
(4, 'Unisex', 1);

create table compania_transporte(
id_transporte bigint auto_increment primary key,
transport_nombre varchar(100) not null unique,
transport_estado int not null default 1
);

create table forma_pago(
id_fpago bigint auto_increment primary key,
fpago_metodo varchar(15) unique,
fpago_estado int not null default 1
);

create table tipo_venta(
id_tipoventa bigint auto_increment primary key,
tipoventa_nombre varchar(50) not null unique,
tipoventa_estado int not null default 1
);

create table marca(
id_marca bigint auto_increment primary key,
marca_nombre varchar(100) not null,
marca_fecha date not null,
marca_estado int not null default 1
);

INSERT INTO `marca` (`id_marca`, `marca_nombre`, `marca_fecha`, `marca_estado`) VALUES
(1, 'Marca01', '2025-08-26',1),
(2, 'Marca02', '2025-08-26',1),
(3, 'Marca03', '2025-08-26',1),
(4, 'Marca04', '2025-08-26',2);

create table unidad(
id_unidad int auto_increment primary key,
uni_nombre varchar(50) not null unique,
uni_estado int not null default 1
);

INSERT INTO `unidad` (`id_unidad`, `uni_nombre`, `uni_estado`) VALUES
(1, 'Unidades', 1);

create table tipo_documento(
id_tipodocumento bigint auto_increment primary key,
tipodoc_nombre varchar(50) not null unique,
tipodoc_estado int not null default 1
);

-- Volcado de Datos tipo_documento
INSERT INTO `tipo_documento` (`id_tipodocumento`, `tipodoc_nombre`, `tipodoc_estado`) VALUES
(1, 'DNI', 1),
(2, 'RUC', 1),
(3, 'Carné de Extranjería', 1);

create table perfil (
id_perfil bigint auto_increment primary key,
perfil_nombre varchar(50) not null unique,
perfil_descripcion varchar(250),
perfil_estado int not null default 1
);

-- Volcado de Datos Pefiles
INSERT INTO `perfil` (`id_perfil`, `perfil_nombre`, `perfil_descripcion`, `perfil_estado`) VALUES
(1, 'Administrador', 'Acceso total al sistema.', 1),
(2, 'Editor', 'Puede gestionar usuarios pero no perfiles.', 1),
(3, 'Supervisor', 'Solo puede visualizar información.', 1);

create table opcion(
id_opcion bigint auto_increment primary key,
opcion_nombre varchar(100) not null unique,
opcion_ruta varchar(100)
);

-- Volcado de Datos Opciones 
INSERT INTO `opcion` (`id_opcion`, `opcion_nombre`, `opcion_ruta`) VALUES
(1, 'Dashboard', '/'),
(2, 'Gestión de Usuarios', '/usuarios/listar'),
(3, 'Gestión de Perfiles', '/perfiles/listar'),
(4, 'Gestión de Categorías', '/categorias/lista'),
(5, 'Gestión de Marcas', '/marcas/lista'),
(6, 'Gestión de Productos', '/productos/lista');

create table perfil_opcion(
id_perfil bigint not null,
id_opcion bigint not null,
primary key (id_perfil,id_opcion),
constraint `FK_Perfil` foreign key (`id_perfil`) references `perfil`(`id_perfil`),
constraint `FK_Opcion` foreign key (`id_opcion`) references `opcion`(`id_opcion`)
);

-- Volcado de Datos Perfil_Opcion
INSERT INTO `perfil_opcion` (`id_perfil`, `id_opcion`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(2, 1),
(2, 2),
(3, 1);

create table empleado (
id_empleado bigint auto_increment primary key,
emple_nombre varchar(100) not null,
emple_nombreuser varchar(50) unique not null,
emple_apellido_paterno varchar(100) not null,
emple_apellido_materno varchar(100) not null,
emple_correo varchar(60) unique not null,
emple_contrasena varchar(150) not null,
emple_telefono varchar(9) unique not null,
emple_direccion varchar(100) not null,
emple_estado int not null default 1,
emple_ndocumento varchar(20) unique not null,
id_tipodocumento bigint not null,
id_perfil bigint not null,
constraint `FK_TipoDoc_Empleado` foreign key (`id_tipodocumento`) references `tipo_documento`(`id_tipodocumento`),
constraint `FK_Perfil_Empleado` foreign key (`id_perfil`) references `perfil`(`id_perfil`)
);

-- Volcado de Datos Empleados
INSERT INTO `empleado` (`id_empleado`, `emple_nombre`, `emple_nombreuser`,`emple_apellido_paterno`,`emple_apellido_materno`, `emple_correo`,`emple_contrasena`,`emple_telefono`,`emple_direccion`, `emple_estado`, `emple_ndocumento`,`id_tipodocumento`, `id_perfil`) VALUES
(1, 'Daryl', 'admin', 'Zamora','Zamora', 'luis@ejemplo.com','$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O','111111111','Direccion Ficticia', 1,'11111111', 1,1);


create table proveedor(
id_proveedor bigint auto_increment primary key,
provee_nombre varchar(100) not null unique,
provee_nombre_comercial varchar(100) not null,
provee_nacionalidad varchar(100) not null,
provee_direccion varchar(100) not null,
provee_telefono char(9) not null,
provee_correo varchar(150) not null,
provee_correo_adicional varchar(150) null,
provee_estado int not null default 1,
provee_ndocumento varchar(20) not null,
id_tipodocumento bigint not null,
constraint `FK_TipoDoc_Proveedor` foreign key (`id_tipodocumento`) references `tipo_documento`(`id_tipodocumento`)
);

create table contacto(
id_contacto bigint auto_increment primary key,
contac_nombre varchar(100) not null,
contac_apellido_paterno varchar(100) not null,
contac_apellido_materno varchar(100) not null,
contac_telefono char(9) not null,
contac_observaciones varchar(300),
id_proveedor bigint not null,
constraint `FK_Proveedor_Contacto` foreign key (`id_proveedor`) references `proveedor`(`id_proveedor`)
);

create table orden(
id_orden bigint auto_increment primary key,
order_fecha date not null,
order_fecha_requerida date not null,
order_fecha_envio date null,
order_nombre_transportista varchar(150) not null,
order_direccion_envio varchar(250) not null,
order_ciudad varchar(100) not null,
order_provincia varchar(100) not null,
id_transporte bigint not null,
id_proveedor bigint not null,
id_empleado bigint not null,
constraint `FK_Transporte_Orden` foreign key (`id_transporte`) references `compania_transporte`(`id_transporte`),
constraint `FK_Proveedor_Orden` foreign key (`id_proveedor`) references `proveedor`(`id_proveedor`),
constraint `FK_Empleado_Orden` foreign key (`id_empleado`) references `empleado`(`id_empleado`)
);

create table producto(
id_producto bigint auto_increment primary key,
produc_nombre varchar(100) not null,
produc_codigo varchar(50) not null,
produc_modelo varchar(100) null,
produc_descripcion varchar(150) not null,
produc_precio decimal default 0.00,
produc_fecha_creacion date null,
produc_fecha_vencimiento date null,
produc_stock int default 0,
produc_stock_minimo int default 1,
produc_imagen varchar(100) null,
produc_estado int not null default 1,
id_unidad int not null,
id_categoria bigint not null,
id_marca bigint not null,
constraint `FK_Unidad_Produc` foreign key (`id_unidad`) references `unidad`(`id_unidad`),
constraint `FK_Catego_Produc` foreign key (`id_categoria`) references `categoria`(`id_categoria`),
constraint `FK_Marca_Produc` foreign key (`id_marca`) references `marca`(`id_marca`)
);

create table detalle_orden(
id_producto bigint not null,
id_orden bigint not null,
detalleorden_precio_original decimal not null,
detalleorden_cantidad int not null,
detalleorden_estado int not null default 1,
primary key (id_producto,id_orden),
constraint `FK_Producto_DetalleOrden` foreign key (`id_producto`) references `producto`(`id_producto`),
constraint `FK_Orden_DetalleOrden` foreign key (`id_orden`) references `orden`(`id_orden`)
);

create table cliente(
id_cliente bigint auto_increment primary key,
cli_mombre varchar(100) not null,
cli_apellido_paterno varchar(100) not null,
cli_apellido_materno varchar(100) not null,
cli_telefono char(9) not null,
cli_estado int not null default 1,
cli_direccion varchar(100) not null,
cli_ndocumento varchar(20) not null,
-- Si el cliente no es una persona Natural
cli_nombre_empresa varchar(100),
cli_direccion_empresa varchar(100),
id_tipodocumento bigint not null,
constraint `FK_TipoDoc_Cliente` foreign key (`id_tipodocumento`) references `tipo_documento`(`id_tipodocumento`)
);

create table venta(
id_venta bigint auto_increment primary key,
venta_fecha date not null,
venta_subtotal decimal default 0.00,
venta_igv decimal default 0.00,
venta_total decimal default 0.00,
id_cliente bigint not null,
id_fpago bigint not null,
id_empleado bigint not null,
id_tipoventa bigint not null,
constraint `FK_Cliente_Venta` foreign key (`id_cliente`) references `cliente`(`id_cliente`),
constraint `FK_FPago_Venta` foreign key (`id_fpago`) references `forma_pago`(`id_fpago`),
constraint `FK_Empleado_Venta` foreign key (`id_empleado`) references `empleado`(`id_empleado`),
constraint `FK_TipoVenta_Venta` foreign key (`id_tipoventa`) references `tipo_venta`(`id_tipoventa`)
);

create table direccion_entrega (
id_entrega bigint auto_increment primary key,
entregadirec_mombre_cliente varchar(100) not null,
entregadirec_telefono char(9) not null,
entregadirec_provincia varchar(100) not null,
entregadirec_direccion varchar(150) not null,
id_cliente bigint not null,
id_venta bigint not null,
constraint `FK_Cliente_Entrega` foreign key (`id_cliente`) references `cliente`(`id_cliente`),
constraint `FK_Venta_Entrega` foreign key (`id_venta`) references `venta`(`id_venta`)
);

create table detalle_venta (
id_detalle bigint auto_increment primary key,
detal_venta_cantidad int not null,
detal_venta_precio decimal default 0.00,
detal_venta_subtotal decimal default 0.00,
id_venta bigint not null,
id_producto bigint not null,
constraint `FK_Venta_DetalletVenta` foreign key (`id_venta`) references `venta`(`id_venta`),
constraint `FK_Produc_DetalletVenta` foreign key (`id_producto`) references `producto`(`id_producto`)
);

-- drop database optica