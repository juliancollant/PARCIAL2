
class DBHelper {
    private static String url = "jdbc:mysql://localhost:33061/sistemaventas";
    private static String usuario = "root";
    private static String passw = "";

    private static Connection conn;
    private static Statement stat;

    public static void consultaSinRes(String consultaParam) {
        try {
            conn = DriverManager.getConnection(url, usuario, passw);
            stat = conn.createStatement();
            stat.executeUpdate(consultaParam);

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    public static ResultSet consultaConRes(String consultaParam){
        ResultSet resultado = null;
        try {
            conn = DriverManager.getConnection(url, usuario, passw);
            stat = conn.createStatement();
            resultado = stat.executeQuery(consultaParam);

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return resultado;
    }
}

class Comerciales {

    public static ArrayList<Vendedor> listadoDeVendedores() {
        ArrayList<Vendedor> listaVendedores = new ArrayList<>();

        try {
            String consulta = "SELECT * FROM vendedores";
            ResultSet resultado = DBHelper.consultaConRes(consulta);

            while (resultado.next()) {
                Vendedor vendedor = new Vendedor(resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("apellido"),
                        resultado.getDouble("dni"),
                        resultado.getString("fecha_nacimiento"),
                        resultado.getString("fecha_contratacion"));

                listaVendedores.add(vendedor);
            }

            resultado.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener el listado de vendedores: " + e.getMessage());
        }

        return listaVendedores;
    }

    public static void main(String[] args) {
        ArrayList<Vendedor> vendedores = listadoDeVendedores();

        for (Vendedor vendedor : vendedores) {
            System.out.println(vendedor.toString());
        }

        Productos.generarInforme();
        Producto productoMasVendido = Productos.obtenerProductoMasVendido();
        System.out.println("Producto más vendido: " + productoMasVendido.toString());


    }
}

class Productos {
    public static void generarInforme() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:33061/sistemaventas", "root", "");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM productos")) {

            System.out.println("Informe de Productos en Stock:");
            System.out.println("Producto                        Stock    Precio     Total");
            System.out.println("----------------------------------------------------------");

            double totalValorProductos = 0;

            while (resultSet.next()) {
                String nombreProducto = resultSet.getString("nombre");
                int stock = resultSet.getInt("stock");
                double precioUnidad = resultSet.getDouble("precio");
                double valorTotal = stock * precioUnidad;

                totalValorProductos += valorTotal;

                System.out.printf("%-30s %-8d %-10.2f %-10.2f\n", nombreProducto, stock, precioUnidad, valorTotal);
            }

            System.out.println("----------------------------------------------------------");
            System.out.printf("%-39s%-10.2f\n", "Total:", totalValorProductos);

        } catch (SQLException e) {
            System.out.println("Error al generar el informe: " + e.getMessage());
        }
    }

    public static Producto obtenerProductoMasVendido() {
        Producto productoMasVendido = null;
        String consulta = "SELECT producto_id, SUM(cantidad_vendida) as total_vendido FROM ventas GROUP BY producto_id ORDER BY total_vendido DESC LIMIT 1";
        ResultSet resultado = DBHelper.consultaConRes(consulta);

        try {
            if (resultado.next()) {
                int productoIDMasVendido = resultado.getInt("producto_id");
                productoMasVendido = obtenerProducto(productoIDMasVendido);
            }

            resultado.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto más vendido: " + e.getMessage());
        }

        return productoMasVendido;
    }

    public static Producto obtenerProducto(int productoID) {
        Producto product = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:33061/sistemaventas", "root", "");
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM productos WHERE producto_id = ?");
            stat.setInt(1, productoID);

            ResultSet rs = stat.executeQuery();

            if (rs.next()) {
                product = new Producto(rs.getInt("producto_id"),
                        rs.getInt("stock"),
                        rs.getString("nombre_producto"),
                        rs.getFloat("precio"));
            }

            rs.close();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto: " + e.getMessage());
        }
        return product;
    }
}

class Producto {
    private int id, stock;
    private String nombre;
    private float precio;

    public Producto(int id, int stock, String nombre, float precio) {
        this.id = id;
        this.stock = stock;
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", stock=" + stock +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}

class Vendedor {
    private int id;
    private String nombre, apellido, fechaNac, fechaContratacion;
    private double dni;

    public Vendedor(int id, String nombre, String apellido, double dni, String fechaNac, String fechaContratacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNac = fechaNac;
        this.fechaContratacion = fechaContratacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(String fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public double getDni() {
        return dni;
    }

    public void setDni(double dni) {
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "Vendedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", fechaNac='" + fechaNac + '\'' +
                ", fechaContratacion='" + fechaContratacion + '\'' +
                ", dni=" + dni +
                '}';
    }
}
