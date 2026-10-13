package org.example.proyectocrud.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.proyectocrud.database.DatabaseConnection;
import org.example.proyectocrud.model.Categoria;
import org.example.proyectocrud.model.Marca;
import org.example.proyectocrud.model.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductosController {
    @FXML
    private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<Marca> cmbMarca;
    @FXML private DatePicker dtpFechaCreacion;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtCodigoNombre;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;
    @FXML private DatePicker dtpFechaDesde;
    @FXML private DatePicker dtpFechaHasta;
    @FXML private TextField txtPrecioMinimo;
    @FXML private TextField txtPrecioMaximo;
    @FXML private CheckBox chkProductosExistentes;
    @FXML private ComboBox<String> cmbOrdenarPor;

    // Definición TableView
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, LocalDate> colFechaCreacion;

    // Listas observables
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        cargarCategorias();
        cargarMarcas();
        cargarOpcionesOrden();
        configurarTableview();
        configurarSeleccionTableView();
        cargarProductos();
    }

    private void configurarTableview(){
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));
        colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        // CORRECCIÓN: SimpleObjectProperty para LocalDate
        colFechaCreacion.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFechaCreacion()));

        tblProductos.setItems(listaProductos);
    }

    private void configurarSeleccionTableView(){
        tblProductos.getSelectionModel().selectedItemProperty().addListener((observableValue, oldvable, newValue) -> {
            if (newValue != null) {
                cargarProductoEnFormulario(newValue);
            }
        });
    }

    private void cargarProductoEnFormulario(Producto p){
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dtpFechaCreacion.setValue(p.getFechaCreacion());

        // CORRECCIÓN: Asignar a cmbCategoria
        for(Categoria categoria: listaCategorias) {
            if (categoria.getIdCategoria() == p.getIdCategoria()) {
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        for(Marca marca: listaMarcas) {
            if (marca.getIdMarca() == p.getIdMarca()) {
                cmbMarca.setValue(marca);
                break;
            }
        }
    }

    private void cargarCategorias(){
        listaCategorias.clear();
        String sql = "SELECT id_categoria, nombre FROM catalogos.categorias WHERE estado = 1 ORDER BY nombre DESC;";

        try(Connection conexion = DatabaseConnection.getConnection();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nombre"));
                listaCategorias.add(categoria);
            }
            cmbCategoria.setItems(FXCollections.observableArrayList(listaCategorias));
            cmbFiltroCategoria.setItems(FXCollections.observableArrayList(listaCategorias));
        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar las categorias.", e);
        }
    }

    private void cargarMarcas(){
        listaMarcas.clear();
        String sql = "SELECT id_marca, nombre FROM catalogos.marcas WHERE estado = 1 ORDER BY nombre DESC;";

        try(Connection conexion = DatabaseConnection.getConnection();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                Marca marca = new Marca(rs.getInt("id_marca"), rs.getString("nombre"));
                listaMarcas.add(marca);
            }
            cmbMarca.setItems(FXCollections.observableArrayList(listaMarcas));
        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar las marcas.", e);
        }
    }

    private void cargarOpcionesOrden() {
        cmbOrdenarPor.setItems(FXCollections.observableArrayList(
                "Nombre", "Precio menor a mayor", "Precio mayor a menor",
                "Fecha más reciente", "Fecha más antigua", "Stock mayor a menor", "Stock menor a mayor"
        ));
        cmbOrdenarPor.setValue("Nombre");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarErrorBaseDatos(String mensaje, SQLException e) {
        e.printStackTrace();
        mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", mensaje + "\n\nDetalle:\n" + e.getMessage());
    }

    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Producto(

                rs.getInt(
                        "id_producto"
                ),

                rs.getString(
                        "codigo"
                ),

                rs.getString(
                        "nombre"
                ),

                rs.getString(
                        "descripcion"
                ),

                rs.getDouble(
                        "precio"
                ),

                rs.getInt(
                        "stock"
                ),

                rs.getDate(
                        "fecha_creacion"
                ).toLocalDate(),

                rs.getInt(
                        "id_categoria"
                ),

                rs.getString(
                        "categoria"
                ),

                rs.getInt(
                        "id_marca"
                ),

                rs.getString(
                        "marca"
                ),

                rs.getInt(
                        "estado"
                )
        );
    }



    @FXML
    private void nuevoProducto(){

    }

    @FXML
    private void actualizarProducto(){

    }

    @FXML
    private void eliminarProducto(){

    }

    @FXML
    private void buscarProductos() {

        listaProductos.clear();

        /*
         * La consulta inicia con los INNER JOIN necesarios
         * y con una condición permanente:
         *
         * p.estado = 1
         *
         * De esta forma nunca mostramos productos
         * eliminados lógicamente.
         */
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    c.id_categoria,
                    c.nombre AS categoria,
                    m.id_marca,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas m
                    ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                """);
        /*
         * Aquí almacenaremos los valores que posteriormente
         * serán enviados al PreparedStatement.
         */
        List<Object> parametros =
                new ArrayList<>();

        // Filtro por código o por nombre
        String texto =
                txtCodigoNombre.getText().trim();

        if (!texto.isBlank()) {

            /*
             * OR permite que el texto coincida con código
             * o nombre.
             *
             * ILIKE permite búsquedas parciales sin importar
             * mayúsculas y minúsculas.
             */
            sql.append("""            
                    AND (
                        p.codigo ILIKE ?
                        OR p.nombre ILIKE ?
                    )
                    """);

            parametros.add(
                    "%" + texto + "%"
            );

            parametros.add(
                    "%" + texto + "%"
            );
        }

        // Filtro por fecha desde
        LocalDate fechaDesde =
                dtpFechaDesde.getValue();

        if (fechaDesde != null) {
            sql.append("""
                    AND p.fecha_creacion >= ?
                    """);
            parametros.add(fechaDesde);
        }

        // Filtro por fechahasta
        LocalDate fechaHasta =
                dtpFechaHasta.getValue();

        if (fechaHasta != null) {

            sql.append("""
                    
                    AND p.fecha_creacion <= ?
                    """);

            parametros.add(fechaHasta);
        }

        /*
         * Verificamos que las fechas tengan sentido.
         *
         * "Desde" no debería ser posterior a "Hasta".
         */
        if (
                fechaDesde != null
                        &&
                        fechaHasta != null
                        &&
                        fechaDesde.isAfter(fechaHasta)
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de fechas incorrecto",
                    "La fecha inicial no puede ser posterior a la fecha final."
            );

            return;
        }

        // Aplicación del ORDER BY

        /*
         * ORDER BY no utiliza un parámetro ? para nombres
         * de columnas.
         *
         * Por esta razón utilizamos solamente opciones
         * previamente definidas en el ComboBox.
         *
         * Esto funciona como una whitelist.
         */
        String orden =
                cmbOrdenarPor.getValue();

        if (orden == null) {
            orden = "Nombre";
        }

        switch (orden) {

            case "Precio menor a mayor" ->
                    sql.append("""
                            
                            ORDER BY p.precio ASC
                            """);

            case "Precio mayor a menor" ->
                    sql.append("""
                            
                            ORDER BY p.precio DESC
                            """);

            case "Fecha más reciente" ->
                    sql.append("""
                            
                            ORDER BY p.fecha_creacion DESC
                            """);

            case "Fecha más antigua" ->
                    sql.append("""
                            
                            ORDER BY p.fecha_creacion ASC
                            """);

            case "Stock mayor a menor" ->
                    sql.append("""
                            
                            ORDER BY p.stock DESC
                            """);

            case "Stock menor a mayor" ->
                    sql.append("""
                            
                            ORDER BY p.stock ASC
                            """);

            default ->
                    sql.append("""
                            
                            ORDER BY p.nombre ASC
                            """);
        }

        //Ejecución de la consulta

        try (
                Connection conexion =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {

            /*
             * Recorremos la lista de parámetros para
             * asignarlos dinámicamente al PreparedStatement.
             */
            for (
                    int i = 0;
                    i < parametros.size();
                    i++
            ) {
                Object valor =
                        parametros.get(i);

                if (valor instanceof LocalDate fecha) {

                    ps.setDate(
                            i + 1,
                            Date.valueOf(fecha)
                    );

                } else if (valor instanceof BigDecimal numero) {

                    ps.setBigDecimal(
                            i + 1,
                            numero
                    );

                } else {
                    ps.setObject(
                            i + 1,
                            valor
                    );
                }
            }

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    listaProductos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }

            tblProductos.setItems(
                    listaProductos
            );

            /*
             * Podemos informar al estudiante/usuario cuando
             * ningún registro satisface las condiciones.
             */
            if (listaProductos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }
        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo realizar la búsqueda.",
                    e
            );
        }
    }

    @FXML
    private void limpiarFiltros(){

    }

    @FXML
    private void guardarProducto() {
        if (!validarFormulario()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        if (existeCodigo(codigo, null)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código duplicado", "El código " + codigo + " ya pertenece a otro producto.");
            txtCodigo.requestFocus();
            return;
        }

        String sql = """
                INSERT INTO catalogos.productos
                (codigo, nombre, descripcion, precio, stock, fecha_creacion, id_categoria, id_marca, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtDescripcion.getText().trim());
            ps.setBigDecimal(4, new BigDecimal(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            ps.setDate(6, Date.valueOf(dtpFechaCreacion.getValue()));
            ps.setInt(7, cmbCategoria.getValue().getIdCategoria());
            ps.setInt(8, cmbMarca.getValue().getIdMarca());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro guardado", "El producto fue registrado correctamente.");
                limpiarFormulario();
                cargarProductos();
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Registro duplicado", "El código ingresado ya existe en la base de datos.");
            } else {
                mostrarErrorBaseDatos("No se pudo guardar el producto.", e);
            }
        }
    }

    // CORRECCIÓN: Implementación real de validación contra base de datos
    private boolean existeCodigo(String codigo, Integer idProductoActual) {
        String sql = "SELECT COUNT(*) FROM catalogos.productos WHERE codigo = ?";
        if (idProductoActual != null) {
            sql += " AND id_producto != ?";
        }

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            if (idProductoActual != null) {
                ps.setInt(2, idProductoActual);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos("Error al validar código duplicado", e);
        }
        return false;
    }

    // Implementaciones de métodos vacíos
    private void limpiarFormulario(){
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        dtpFechaCreacion.setValue(null);
        cmbCategoria.getSelectionModel().clearSelection();
        cmbMarca.getSelectionModel().clearSelection();
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario(){
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty() ||
                dtpFechaCreacion.getValue() == null || cmbCategoria.getValue() == null || cmbMarca.getValue() == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }

        try {
            Double.parseDouble(txtPrecio.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Formato incorrecto", "El precio y el stock deben ser valores numéricos.");
            return false;
        }
        return true;
    }

    private void cargarProductos() {
        listaProductos.clear();
        String sql = """
            SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, p.stock, p.fecha_creacion, 
                   p.id_categoria, c.nombre AS categoria, p.id_marca, m.nombre AS marca, p.estado 
            FROM catalogos.productos p 
            INNER JOIN catalogos.categorias c ON p.id_categoria = c.id_categoria 
            INNER JOIN catalogos.marcas m ON p.id_marca = m.id_marca
            WHERE p.estado = 1
        """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id_producto"), rs.getString("codigo"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio"), rs.getInt("stock"),
                        rs.getDate("fecha_creacion").toLocalDate(), rs.getInt("id_categoria"),
                        rs.getString("categoria"), rs.getInt("id_marca"), rs.getString("marca"),
                        rs.getInt("estado")
                );
                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos("Error al cargar la lista de productos", e);
        }
    }
}
