package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import models.Vehicule;
import models.Voyageur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ctrl.IControllerForView;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * Ihm principale l'application.
 *
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @version 1.0.0
 */
public class View implements Initializable, IViewForController {

    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAbout;
    @FXML
    private Button btnQuit;
    @FXML
    private Button btnPlacer;
    @FXML
    private Button btnLiberer;
    @FXML
    private ComboBox<Vehicule> cbxVehicules;
    @FXML
    private TextField txtFiltreNomPrenom;
    @FXML
    private TableView<Voyageur> tableViewVoyageursDansCeVehicule;
    @FXML
    private TableView<Voyageur> tableViewVoyageursAPlacer;
    @FXML
    private TextField txtPK;
    @FXML
    private DatePicker dpDateNaissance;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtRue;
    @FXML
    private TextField txtNPA;
    @FXML
    private TextField txtVille;
    @FXML
    private Label txtStatsVehicule;
    @FXML
    private Label txtStatsVoyageurs;

    private IControllerForView refController;
    private final String fxml;
    private Scene principalScene;
    private Stage mainStage;

    public View() {
        // Si vous avez une erreur "Location is not set" c'est que ce chemin est faux.
        fxml = "/views/View.fxml";
        refController = null;
    }

    @Override
    public void start() {
        /*
         * Callback pour le ControllerFactory, quand JavaFX voudra créer le controlleur
         * de vue,
         * viendra dans ce callback et donnera l'instance déjà créée au lieu d'en faire
         * une nouvelle.
         */
        Callback<Class<?>, Object> controllerFactory = type -> {
            return this;
        };

        // Démarrer JavaFX
        Platform.startup(() -> {
            try {
                mainStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
                fxmlLoader.setControllerFactory(controllerFactory);
                Parent root = (Parent) fxmlLoader.load();
                principalScene = new Scene(root);
                mainStage.setScene(principalScene);
                mainStage.setTitle("Module 223 - E1 avec DAO, JPA, filtrage et verrouillage");
                mainStage.setMinWidth(950); // Pour limiter la taille min
                mainStage.setMinHeight(680); // Pour limiter la taille min
                mainStage.getIcons().add(new Image(getClass().getResourceAsStream("res/many-db-icon.png")));
                mainStage.setOnCloseRequest((WindowEvent e) -> {
                    if (afficherQuestionOuiNon("Voulez-vous vraiment quitter l'application ?")) {
                        refController.actionQuitter();
                    } else {
                        e.consume(); // Ne va pas quitter, l'événement est "consommé"
                    }
                });
                initialiserTableaux();
                mainStage.show();
            } catch (IOException ex) {
                System.out.println("Can't start the IHM because : " + ex);
                Platform.exit();
            }
        });
    }

    private void initialiserTableaux() {

        TableColumn<Voyageur, String> colA1 = new TableColumn<>("PK");
        colA1.setCellValueFactory(new PropertyValueFactory<>("pkVoyageur"));
        colA1.setSortable(false);
        colA1.setMinWidth(40);
        colA1.setMaxWidth(40);

        TableColumn<Voyageur, String> colA2 = new TableColumn<>("Nom");
        colA2.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colA2.setSortable(false);
        colA2.setMinWidth(120);

        TableColumn<Voyageur, String> colA3 = new TableColumn<>("Prenom");
        colA3.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colA3.setSortable(false);
        colA3.setMinWidth(120);

        tableViewVoyageursAPlacer.getColumns().add(colA1);
        tableViewVoyageursAPlacer.getColumns().add(colA2);
        tableViewVoyageursAPlacer.getColumns().add(colA3);

        tableViewVoyageursAPlacer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Voyageur, String> colB1 = new TableColumn<>("PK");
        colB1.setCellValueFactory(new PropertyValueFactory<>("pkVoyageur"));
        colB1.setSortable(false);
        colB1.setMinWidth(40);
        colB1.setMaxWidth(40);

        TableColumn<Voyageur, String> colB2 = new TableColumn<>("Nom");
        colB2.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colB2.setSortable(false);
        colB2.setMinWidth(120);

        TableColumn<Voyageur, String> colB3 = new TableColumn<>("Prenom");
        colB3.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colB3.setSortable(false);
        colB3.setMinWidth(120);

        tableViewVoyageursDansCeVehicule.getColumns().add(colB1);
        tableViewVoyageursDansCeVehicule.getColumns().add(colB2);
        tableViewVoyageursDansCeVehicule.getColumns().add(colB3);

        tableViewVoyageursDansCeVehicule.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onBtnLogin(ActionEvent event) {
        refController.actionLogin();
    }

    @FXML
    private void onBtnLogout(ActionEvent event) {
        refController.actionLogout();
    }

    @FXML
    private void onBtnRefresh(ActionEvent event) {
        refController.actionListeRafraichir();
    }

    @FXML
    private void onBtnNew(ActionEvent event) {
        refController.actionVoyageurCreer();
    }

    @FXML
    private void onBtnUpdate(ActionEvent event) {
        List<Voyageur> voyageursSelectionnes = tableViewVoyageursAPlacer.getSelectionModel().getSelectedItems();
        if ((voyageursSelectionnes != null) && (voyageursSelectionnes.size() == 1)) {
            Voyageur voyageurAModifier = voyageursSelectionnes.get(0);

            voyageurAModifier.setNom(txtNom.getText());
            voyageurAModifier.setPrenom(txtPrenom.getText());
            voyageurAModifier.setRue(txtRue.getText());
            voyageurAModifier.setNpa(txtNPA.getText());
            voyageurAModifier.setVille(txtVille.getText());
            java.sql.Date dateFromLocalDate = java.sql.Date.valueOf(dpDateNaissance.getValue());

            voyageurAModifier.setDateNaissance(dateFromLocalDate);

            refController.actionVoyageurModifier(voyageurAModifier);
        }
    }

    @FXML
    private void onBtnDelete(ActionEvent event) {
        List<Voyageur> voyageursSelectionnes = tableViewVoyageursAPlacer.getSelectionModel().getSelectedItems();
        if ((voyageursSelectionnes != null) && (voyageursSelectionnes.size() == 1)) {
            Voyageur voyageurASupprimer = voyageursSelectionnes.get(0);
            refController.actionVoyageurSupprimer(voyageurASupprimer);
        }
    }

    @FXML
    private void onBtnAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("A propos de...");
        alert.setHeaderText(null);
        alert.setContentText("Ecrit par Paul Friedli"
                + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "Buts : être capable de converser avec une base de données SQL distante depuis Java, en tenant compte des problèmes qui peuvent se poser en cas de concurrence d'accès aux données.");
        ImageView icon = new ImageView("file:src/views/res/programming-computer.gif");
        icon.setFitWidth(128);
        icon.setFitHeight(128);
        alert.setGraphic(icon);
        alert.showAndWait();
    }

    @FXML
    private void onBtnQuit(ActionEvent event) {
        mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void onBtnPlacer(ActionEvent event) {
        List<Voyageur> voyageursAPlacer = tableViewVoyageursAPlacer.getSelectionModel().getSelectedItems();
        if ((voyageursAPlacer != null) && !voyageursAPlacer.isEmpty()) {
            Vehicule vehicule = cbxVehicules.getSelectionModel().getSelectedItem();
            if (vehicule != null) {
                refController.actionPlacerVoyageursDansVehicule(vehicule, voyageursAPlacer);
            }
        }
    }

    @FXML
    private void onBtnLiberer(ActionEvent event) {
        List<Voyageur> voyageursALiberer = tableViewVoyageursDansCeVehicule.getSelectionModel().getSelectedItems();
        if ((voyageursALiberer != null) && !voyageursALiberer.isEmpty()) {
            Vehicule vehicule = cbxVehicules.getSelectionModel().getSelectedItem();
            if (vehicule != null) {
                refController.actionLibererVoyageursDuVehicule(vehicule, voyageursALiberer);
            }
        }
    }

    private void onClosing() {
        refController.actionQuitter(); // Pour permettre au Ctrl de faire un dernier truc avant la fin de l'application
    }

    @Override
    public void afficherMessageInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(new ImageView("file:src/views/res/icon-fine-64.png"));
        alert.showAndWait();
    }

    @Override
    public void afficherMessageErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(new ImageView("file:src/views/res/icon-error-64.png"));
        alert.showAndWait();
    }

    @Override
    public boolean afficherQuestionOuiNon(String message) {
        boolean reponseOui = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Question");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(new ImageView("file:src/views/res/icon-question-64.png"));
        ButtonType okButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(noButton) == okButton) {
            reponseOui = true;
        }

        return reponseOui;
    }

    @Override
    public String obtenirValeurFiltreSurNom() {
        return txtFiltreNomPrenom.getText();
    }

    @Override
    public void reinitialiserFiltreSurNom() {
        txtFiltreNomPrenom.setText("");
    }

    @Override
    public void selectionnerVoyageur(Voyageur voyageur) {
        tableViewVoyageursAPlacer.getSelectionModel().clearSelection();
        if (voyageur != null) {
            tableViewVoyageursAPlacer.getSelectionModel().select(voyageur);
            tableViewVoyageursAPlacer.scrollTo(voyageur);
        }
    }

    @Override
    public void montrerEtatActuel(boolean onEstConnecte) {
        Platform.runLater(() -> {
            btnLogin.setDisable(onEstConnecte);
            btnLogout.setDisable(!onEstConnecte);
            btnRefresh.setDisable(!onEstConnecte);

            cbxVehicules.setDisable(!onEstConnecte);

            List<Voyageur> voyageursAPlacerSelectionnes = tableViewVoyageursAPlacer.getSelectionModel()
                    .getSelectedItems();

            btnNew.setDisable(!onEstConnecte);
            btnUpdate.setDisable(!onEstConnecte || (voyageursAPlacerSelectionnes.size() != 1));
            btnDelete.setDisable(!onEstConnecte || (voyageursAPlacerSelectionnes.size() != 1));

            tableViewVoyageursAPlacer.setDisable(!onEstConnecte || (tableViewVoyageursAPlacer.getItems() == null));

            txtPK.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            dpDateNaissance.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            txtNom.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            txtPrenom.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            txtRue.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            txtNPA.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
            txtVille.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));

            txtFiltreNomPrenom.setDisable(!onEstConnecte);

            btnPlacer.setDisable(!onEstConnecte || (voyageursAPlacerSelectionnes.size() < 1)
                    || (cbxVehicules.getSelectionModel().getSelectedIndex() < 0));
            btnLiberer.setDisable(!onEstConnecte
                    || (tableViewVoyageursDansCeVehicule.getSelectionModel().getSelectedItems().size() < 1));

            txtStatsVoyageurs.setDisable(!onEstConnecte);
        });
    }

    @Override
    public void selectionnerVehicule(Vehicule vehicule) {
        cbxVehicules.getSelectionModel().clearSelection();
        cbxVehicules.getSelectionModel().select(vehicule);
    }

    @Override
    public void definirListeVehicules(List<Vehicule> vehicules) {
        if (vehicules != null) {
            cbxVehicules.getItems().setAll(vehicules);
            cbxVehicules.requestFocus();

            cbxVehicules.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

                tableViewVoyageursAPlacer.getSelectionModel().clearSelection();

                tableViewVoyageursDansCeVehicule.getItems().clear();
                if (newValue != null) {
                    tableViewVoyageursDansCeVehicule.getItems().setAll(newValue.getPassagers());
                    txtStatsVehicule.setText(newValue.getPassagers().size() + "/" + newValue.getCapacite());
                } else {
                    txtStatsVehicule.setText("0/0");

                }
                tableViewVoyageursDansCeVehicule.scrollTo(0);

                tableViewVoyageursDansCeVehicule.setDisable(cbxVehicules.getSelectionModel().getSelectedIndex() < 0);
                txtStatsVehicule.setDisable(cbxVehicules.getSelectionModel().getSelectedIndex() < 0);

                tableViewVoyageursDansCeVehicule.getSelectionModel().getSelectedItems()
                        .addListener(new ListChangeListener<Voyageur>() {
                            @Override
                            public void onChanged(Change<? extends Voyageur> change) {
                                Platform.runLater(() -> {
                                    List<Voyageur> voyageursDeCeVehiculeSelectionnes = tableViewVoyageursDansCeVehicule
                                            .getSelectionModel().getSelectedItems();
                                    btnLiberer.setDisable(
                                            btnLogout.isDisabled() || (voyageursDeCeVehiculeSelectionnes.size() < 1)
                                                    || (cbxVehicules.getSelectionModel().getSelectedIndex() < 0));
                                });
                            }
                        });

            });

            cbxVehicules.getSelectionModel().selectFirst();
        } else {
            cbxVehicules.getItems().clear();
        }
    }

    @Override
    public void definirListeVoyageursAPlacer(List<Voyageur> voyageursAPlacer) {

        txtStatsVoyageurs.setText((voyageursAPlacer != null) ? "" + voyageursAPlacer.size() : "0");

        txtStatsVoyageurs.setDisable(voyageursAPlacer == null);

        tableViewVoyageursAPlacer.setDisable(voyageursAPlacer == null);
        txtFiltreNomPrenom.setDisable(voyageursAPlacer == null);

        tableViewVoyageursAPlacer.getItems().clear();
        if (voyageursAPlacer != null) {
            btnNew.setDisable(false);
            tableViewVoyageursAPlacer.getItems().setAll(voyageursAPlacer);
            tableViewVoyageursAPlacer.scrollTo(0);

            tableViewVoyageursAPlacer.getSelectionModel().getSelectedItems()
                    .addListener(new ListChangeListener<Voyageur>() {
                        @Override
                        public void onChanged(Change<? extends Voyageur> change) {
                            Platform.runLater(() -> {
                                List<Voyageur> voyageursAPlacerSelectionnes = tableViewVoyageursAPlacer
                                        .getSelectionModel().getSelectedItems();

                                btnUpdate.setDisable(
                                        btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                btnDelete.setDisable(
                                        btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));

                                btnPlacer.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() < 1)
                                        || (cbxVehicules.getSelectionModel().getSelectedIndex() < 0));

                                // vider champs
                                txtPK.setText("");
                                dpDateNaissance.setValue(LocalDate.of(1970, 1, 1));
                                txtNom.setText("");
                                txtPrenom.setText("");
                                txtRue.setText("");
                                txtNPA.setText("");
                                txtVille.setText("");

                                txtPK.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                dpDateNaissance.setDisable(
                                        btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                txtNom.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                txtPrenom.setDisable(
                                        btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                txtRue.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                txtNPA.setDisable(btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));
                                txtVille.setDisable(
                                        btnLogout.isDisabled() || (voyageursAPlacerSelectionnes.size() != 1));

                                if (voyageursAPlacerSelectionnes.size() == 1) {
                                    Voyageur voyageurActuel = voyageursAPlacerSelectionnes.get(0);
                                    if (voyageurActuel != null) {
                                        // remplir champs
                                        txtPK.setText("");
                                        try {
                                            txtPK.setText(String.valueOf(voyageurActuel.getPkVoyageur()));
                                        } catch (Exception e) {
                                        }
                                        dpDateNaissance.setValue(voyageurActuel.getDateNaissance().toLocalDate());
                                        txtNom.setText(voyageurActuel.getNom());
                                        txtPrenom.setText(voyageurActuel.getPrenom());
                                        txtRue.setText(voyageurActuel.getRue());
                                        txtNPA.setText(voyageurActuel.getNpa());
                                        txtVille.setText(voyageurActuel.getVille());
                                    }
                                }
                            });
                        }
                    });
        } else {
            btnNew.setDisable(true);
        }
    }

    public IControllerForView getRefController() {
        return refController;
    }

    public void setRefController(IControllerForView refController) {
        this.refController = refController;
    }

}
