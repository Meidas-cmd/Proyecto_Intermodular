package com.example.proyecto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {


    @FXML private TextField tfAlimento;
    @FXML private TextField tfCalorias;
    @FXML private TextField tfCantidad;
    @FXML private TableView<AlimentoEntry> tablaAlimentos;
    @FXML private TableColumn<AlimentoEntry, String> colAlimento;
    @FXML private TableColumn<AlimentoEntry, Integer> colCalorias;
    @FXML private TableColumn<AlimentoEntry, Double> colCantidad;
    @FXML private TableColumn<AlimentoEntry, Double> colTotal;
    @FXML private Label lblTotalCalorias;
    @FXML private ProgressBar progressCalorias;
    @FXML private TextField tfMetaCalorias;


    @FXML private TextField tfNombreReceta;
    @FXML private TextArea taIngredientes;
    @FXML private TextArea taPasos;
    @FXML private TextField tfCaloriasReceta;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ListView<String> listaRecetas;
    @FXML private Label lblDetalleReceta;
    @FXML private VBox vboxDetalle;


    private ObservableList<AlimentoEntry> alimentosList = FXCollections.observableArrayList();
    private List<Receta> recetasList = new ArrayList<>();
    private double metaCalorias = 2000.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaAlimentos();
        configurarRecetas();
        cargarRecetasEjemplo();
    }

    private void configurarTablaAlimentos() {
        colAlimento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCalorias.setCellValueFactory(new PropertyValueFactory<>("caloriasPor100g"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadGramos"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("caloriasTotal"));
        tablaAlimentos.setItems(alimentosList);
    }

    private void configurarRecetas() {
        cbCategoria.setItems(FXCollections.observableArrayList(
                "Desayuno", "Almuerzo", "Cena", "Merienda", "Postre", "Bebida"
        ));
        cbCategoria.getSelectionModel().selectFirst();

        listaRecetas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarDetalleReceta(newVal)
        );
    }

    private void cargarRecetasEjemplo() {
        agregarRecetaInterna("Tortilla Española",
                "3 huevos, 2 patatas medianas, 1 cebolla, aceite de oliva, sal",
                "1. Pelar y cortar las patatas en láminas finas.\n2. Pochar las patatas y la cebolla en aceite.\n3. Batir los huevos y mezclar con las patatas.\n4. Cuajar en sartén antiadherente.",
                380, "Almuerzo");

        agregarRecetaInterna("Batido Proteico",
                "200ml leche, 1 plátano, 2 cucharadas proteína en polvo, 1 cucharada mantequilla cacahuete",
                "1. Poner todos los ingredientes en la batidora.\n2. Triturar hasta obtener textura suave.\n3. Servir frío.",
                320, "Desayuno");

        agregarRecetaInterna("Ensalada César",
                "Lechuga romana, pollo a la plancha, picatostes, parmesano, salsa César",
                "1. Lavar y cortar la lechuga.\n2. Añadir el pollo en tiras.\n3. Incorporar picatostes y parmesano.\n4. Aliñar con salsa César.",
                420, "Cena");
    }

    @FXML
    private void onAgregarAlimento() {
        String nombre = tfAlimento.getText().trim();
        String calStr = tfCalorias.getText().trim();
        String cantStr = tfCantidad.getText().trim();

        if (nombre.isEmpty() || calStr.isEmpty() || cantStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, rellena todos los campos.");
            return;
        }

        try {
            int calorias = Integer.parseInt(calStr);
            double cantidad = Double.parseDouble(cantStr);

            if (calorias < 0 || cantidad <= 0) {
                mostrarAlerta("Valor inválido", "Las calorías y la cantidad deben ser positivas.");
                return;
            }

            AlimentoEntry entry = new AlimentoEntry(nombre, calorias, cantidad);
            alimentosList.add(entry);
            actualizarTotalCalorias();
            limpiarCamposAlimento();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Introduce números válidos en calorías y cantidad.");
        }
    }

    @FXML
    private void onEliminarAlimento() {
        AlimentoEntry seleccionado = tablaAlimentos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            alimentosList.remove(seleccionado);
            actualizarTotalCalorias();
        } else {
            mostrarAlerta("Sin selección", "Selecciona un alimento para eliminar.");
        }
    }

    @FXML
    private void onLimpiarTodo() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar todos los alimentos del día?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                alimentosList.clear();
                actualizarTotalCalorias();
            }
        });
    }

    @FXML
    private void onActualizarMeta() {
        try {
            metaCalorias = Double.parseDouble(tfMetaCalorias.getText().trim());
            actualizarTotalCalorias();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Introduce un número válido para la meta calórica.");
        }
    }

    private void actualizarTotalCalorias() {
        double total = alimentosList.stream()
                .mapToDouble(AlimentoEntry::getCaloriasTotal)
                .sum();

        lblTotalCalorias.setText(String.format("Total: %.0f kcal", total));

        double progreso = Math.min(total / metaCalorias, 1.0);
        progressCalorias.setProgress(progreso);

        // Cambiar color de la barra según el porcentaje
        if (progreso < 0.6) {
            progressCalorias.setStyle("-fx-accent: #4CAF50;");
        } else if (progreso < 0.9) {
            progressCalorias.setStyle("-fx-accent: #FF9800;");
        } else {
            progressCalorias.setStyle("-fx-accent: #F44336;");
        }
    }

    private void limpiarCamposAlimento() {
        tfAlimento.clear();
        tfCalorias.clear();
        tfCantidad.clear();
        tfAlimento.requestFocus();
    }

    @FXML
    private void onGuardarReceta() {
        String nombre = tfNombreReceta.getText().trim();
        String ingredientes = taIngredientes.getText().trim();
        String pasos = taPasos.getText().trim();
        String calStr = tfCaloriasReceta.getText().trim();
        String categoria = cbCategoria.getValue();

        if (nombre.isEmpty() || ingredientes.isEmpty() || pasos.isEmpty() || calStr.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Rellena todos los campos de la receta.");
            return;
        }

        try {
            int calorias = Integer.parseInt(calStr);
            agregarRecetaInterna(nombre, ingredientes, pasos, calorias, categoria);
            limpiarCamposReceta();
            mostrarInfo("Receta guardada", "La receta \"" + nombre + "\" ha sido añadida.");
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Las calorías deben ser un número entero.");
        }
    }

    @FXML
    private void onEliminarReceta() {
        String seleccionada = listaRecetas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Sin selección", "Selecciona una receta para eliminar.");
            return;
        }

        recetasList.removeIf(r -> r.getNombre().equals(seleccionada));
        listaRecetas.getItems().remove(seleccionada);
        lblDetalleReceta.setText("Selecciona una receta para ver los detalles.");
    }

    @FXML
    private void onAnadirRecetaADia() {
        String seleccionada = listaRecetas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Sin selección", "Selecciona una receta para añadir al día.");
            return;
        }

        // Buscamos la receta en la lista
        recetasList.stream()
                .filter(r -> r.getNombre().equals(seleccionada))
                .findFirst()
                .ifPresent(receta -> {
                    // Pedimos la cantidad al usuario mediante un diálogo
                    TextInputDialog dialog = new TextInputDialog("100"); // 100g por defecto
                    dialog.setTitle("Cantidad de la receta");
                    dialog.setHeaderText("Añadir " + receta.getNombre());
                    dialog.setContentText("¿Cuántos gramos vas a consumir?:");

                    dialog.showAndWait().ifPresent(cantidadStr -> {
                        try {
                            double cantidad = Double.parseDouble(cantidadStr);
                            if (cantidad <= 0) {
                                mostrarAlerta("Error", "La cantidad debe ser mayor que 0.");
                                return;
                            }

                            // Añadir la receta con la cantidad elegida
                            // Usamos las calorías de la receta como "calorías por 100g" para que el cálculo sea correcto
                            AlimentoEntry entry = new AlimentoEntry(
                                    "🍽 " + receta.getNombre(),
                                    receta.getCalorias(),
                                    cantidad
                            );

                            alimentosList.add(entry);
                            actualizarTotalCalorias();

                        } catch (NumberFormatException e) {
                            mostrarAlerta("Error de formato", "Introduce un número válido para los gramos.");
                        }
                    });
                });
    }

    private void agregarRecetaInterna(String nombre, String ingredientes,
                                      String pasos, int calorias, String categoria) {
        Receta r = new Receta(nombre, ingredientes, pasos, calorias, categoria);
        recetasList.add(r);
        listaRecetas.getItems().add(nombre);
    }

    private void mostrarDetalleReceta(String nombreReceta) {
        if (nombreReceta == null) return;

        recetasList.stream()
                .filter(r -> r.getNombre().equals(nombreReceta))
                .findFirst()
                .ifPresent(r -> {
                    String detalle = String.format(
                            "📌 %s  |  %s  |  %d kcal\n\n" +
                                    "🛒 Ingredientes:\n%s\n\n" +
                                    "👨‍🍳 Preparación:\n%s",
                            r.getNombre(), r.getCategoria(), r.getCalorias(),
                            r.getIngredientes(), r.getPasos()
                    );
                    lblDetalleReceta.setText(detalle);
                });
    }

    private void limpiarCamposReceta() {
        tfNombreReceta.clear();
        taIngredientes.clear();
        taPasos.clear();
        tfCaloriasReceta.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class AlimentoEntry {
        private String nombre;
        private int caloriasPor100g;
        private double cantidadGramos;

        public AlimentoEntry(String nombre, int caloriasPor100g, double cantidadGramos) {
            this.nombre = nombre;
            this.caloriasPor100g = caloriasPor100g;
            this.cantidadGramos = cantidadGramos;
        }

        public String getNombre() { return nombre; }
        public int getCaloriasPor100g() { return caloriasPor100g; }
        public double getCantidadGramos() { return cantidadGramos; }
        public double getCaloriasTotal() {
            return (caloriasPor100g * cantidadGramos) / 100.0;
        }

        public void setNombre(String n) { this.nombre = n; }
        public void setCaloriasPor100g(int c) { this.caloriasPor100g = c; }
        public void setCantidadGramos(double g) { this.cantidadGramos = g; }
    }

    public static class Receta {
        private String nombre;
        private String ingredientes;
        private String pasos;
        private int calorias;
        private String categoria;

        public Receta(String nombre, String ingredientes,
                      String pasos, int calorias, String categoria) {
            this.nombre = nombre;
            this.ingredientes = ingredientes;
            this.pasos = pasos;
            this.calorias = calorias;
            this.categoria = categoria;
        }

        public String getNombre() { return nombre; }
        public String getIngredientes() { return ingredientes; }
        public String getPasos() { return pasos; }
        public int getCalorias() { return calorias; }
        public String getCategoria() { return categoria; }
    }
}
