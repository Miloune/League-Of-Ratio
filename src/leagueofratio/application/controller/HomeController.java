/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leagueofratio.application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import leagueofratio.application.object.FilterComboBox;
import leagueofratio.application.object.NumberField;
import leagueofratio.object.Champion;
import leagueofratio.object.Division;
import leagueofratio.object.Game;
import leagueofratio.object.Rank;
import leagueofratio.object.Result;
import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Miloune
 */
public class HomeController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private PieChart pieChart;
    @FXML
    private ChoiceBox choiceRank;
    @FXML
    private ChoiceBox choiceDivision;
    @FXML
    private Label caption;

    @FXML
    private TableView<Game> tableView;
    @FXML
    private TableColumn<Game, Champion> tableChampion;
    @FXML
    private TableColumn<Game, Result> tableResult;
    @FXML
    private TableColumn<Game, Integer> tableKill;
    @FXML
    private TableColumn<Game, Integer> tableDeath;
    @FXML
    private TableColumn<Game, Integer> tableAssist;
    @FXML
    private TableColumn<Game, Integer> tableKda;
    @FXML
    private TableColumn<Game, Integer> tableElo;
    @FXML
    private TableColumn<Game, Rank> tableRanked;
    @FXML
    private TableColumn<Game, Division> tableDivision;

    @FXML
    private Label averageKdaSelected;
    @FXML
    private PieChart pieChartSelected;
    @FXML
    private BarChart barChartSelected;

    private ArrayList<Game> allGames;
    private final ArrayList<Game> allGamesFiltered = new ArrayList<>();
    private FilterComboBox choiceChampion;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeAllGames();
        initializeChoiceChampion();
        initializeChoiceRank();
        initializeChoiceDivision();
        initializeTableView();

        refreshData();
    }

    private void setPieChartWinLoose(int win, int loose, String title) {
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("Victory", win),
                        new PieChart.Data("Defeat", loose));
        pieChart.setTitle(title);
        pieChart.setData(pieChartData);
        pieChart.setLabelLineLength(2);
        pieChart.setLegendSide(Side.LEFT);

        applyCustomColorSequence(
                pieChartData,
                "green",
                "red"
        );
    }

    private void initializeTableView() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableChampion.setCellValueFactory(new PropertyValueFactory<>("champion"));
        tableResult.setCellValueFactory(new PropertyValueFactory<>("result"));
        tableKill.setCellValueFactory(new PropertyValueFactory<>("kill"));
        tableDeath.setCellValueFactory(new PropertyValueFactory<>("death"));
        tableAssist.setCellValueFactory(new PropertyValueFactory<>("assist"));
        tableKda.setCellValueFactory(new PropertyValueFactory<>("kda"));
        tableElo.setCellValueFactory(new PropertyValueFactory<>("elo"));
        tableRanked.setCellValueFactory(new PropertyValueFactory<>("rank"));
        tableDivision.setCellValueFactory(new PropertyValueFactory<>("division"));

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    filterSelectedItem();
                }
            }
        });
    }

    private void initializeAllGames() {
        try {
            allGames = Game.getAllGames();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeChoiceChampion() {
        ObservableList<Champion> oListChampion = FXCollections.observableArrayList(Champion.values());
        ObservableList<String> oListChampionString = FXCollections.observableArrayList();
        oListChampionString.add("All Champions");
        oListChampion.stream().forEach((champion) -> {
            oListChampionString.add(champion.name());
        });
        choiceChampion = new FilterComboBox(oListChampionString);
        choiceChampion.setLayoutX(14);
        choiceChampion.setLayoutY(244);
        choiceChampion.setPrefWidth(200);

        choiceChampion.getSelectionModel().selectFirst();
        anchorPane.getChildren().add(choiceChampion);
    }

    private void initializeChoiceRank() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("All rank");
        list.add(Rank.CHALLENGER);
        list.add(Rank.DIAMOND);
        list.add(Rank.PLATINIUM);
        list.add(Rank.GOLD);
        list.add(Rank.SILVER);
        list.add(Rank.BRONZE);
        choiceRank.getItems().addAll(list);

        choiceRank.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
            if ((int) number2 == 0 || (int) number2 == 1) {
                choiceDivision.setVisible(false);
            } else {
                choiceDivision.setVisible(true);
            }
        });

        choiceRank.getSelectionModel().selectFirst();
    }

    private void initializeChoiceDivision() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("All Division");
        list.add(Division.I);
        list.add(Division.II);
        list.add(Division.III);
        list.add(Division.IV);
        list.add(Division.V);
        choiceDivision.getItems().addAll(list);

        choiceDivision.getSelectionModel().selectFirst();
    }

    private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle("-fx-pie-color: " + pieColors[i % pieColors.length] + ";");
            i++;
        }

        pieChartData.stream().forEach((data) -> {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                caption.setTextFill(Color.BLACK);
                caption.setStyle("-fx-font: 12 arial; -fx-font-weight:bold;");
                caption.setTranslateX(e.getSceneX() - 20);
                caption.setTranslateY(e.getSceneY() - 20);
                caption.setText(String.valueOf((int) data.getPieValue()) + " game(s)");
                caption.setVisible(true);

                Object synchro = new Object();
                Thread timeoutTask = new Thread() {
                    @Override
                    public void run() {
                        synchronized (synchro) {
                            try {
                                synchro.wait(3000); // Wait 3s
                                Platform.runLater(() -> {
                                    caption.setVisible(false);
                                });

                            } catch (InterruptedException e) {
                            }
                        }
                    }
                };
                timeoutTask.start();
            });
        });
    }

    private void refreshData() {
        applyFilter();
    }

    @FXML
    private void addGame() {
        ObservableList<Champion> oListChampion = FXCollections.observableArrayList(Champion.values());
        ObservableList<String> oListChampionString = FXCollections.observableArrayList();
        oListChampion.stream().forEach((champion) -> {
            oListChampionString.add(champion.name());
        });
        final FilterComboBox champion = new FilterComboBox(oListChampionString);

        ObservableList<Result> oListBooleans = FXCollections.observableArrayList(Result.values());
        final ComboBox result = new ComboBox(oListBooleans);

        ObservableList<Rank> oListRank = FXCollections.observableArrayList(Rank.values());
        final ComboBox ranked = new ComboBox(oListRank);

        ObservableList<Division> oListDivision = FXCollections.observableArrayList(Division.values());
        final ComboBox division = new ComboBox(oListDivision);

        final NumberField kill = new NumberField();
        final NumberField death = new NumberField();
        final NumberField assist = new NumberField();
        final NumberField elo = new NumberField();

        final Action actionAdd;
        actionAdd = new AbstractAction("Add") {
            // This method is called when the login button is clicked ...
            @Override
            public void handle(ActionEvent ae) {
                Dialog d = (Dialog) ae.getSource();

                if (champion.getValue() == null || result.getValue() == null
                        || ranked.getValue() == null || division.getValue() == null
                        || kill.getText().isEmpty() || death.getText().isEmpty()
                        || assist.getText().isEmpty() || elo.getText().isEmpty()) {
                    Dialogs.create()
                            .owner(null)
                            .title("Be careful")
                            .masthead("Don't let a single empty!")
                            .message("You must fill all fields...")
                            .showError();
                } else {
                    Game game = new Game(Champion.valueOf(champion.getValue()),
                            (Result) result.getValue(), kill.getInt().doubleValue(),
                            death.getInt().doubleValue(), assist.getInt().doubleValue(),
                            elo.getInt(), (Rank) ranked.getValue(), (Division) division.getValue());
                    try {
                        Game.saveGame(game);
                    } catch (IOException ex) {
                        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    allGames.add(game);

                    refreshData();
                    d.hide();
                }
            }
        };

        Dialog dlg = new Dialog(null, "Add a game");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        grid.add(new Label("Champion:"), 0, 0);
        grid.add(champion, 1, 0);
        grid.add(new Label("Result:"), 0, 1);
        grid.add(result, 1, 1);
        grid.add(new Label("Kill:"), 0, 2);
        grid.add(kill, 1, 2);
        grid.add(new Label("Death:"), 0, 3);
        grid.add(death, 1, 3);
        grid.add(new Label("Assist:"), 0, 4);
        grid.add(assist, 1, 4);
        grid.add(new Label("Elo:"), 0, 5);
        grid.add(elo, 1, 5);
        grid.add(new Label("Ranked:"), 0, 6);
        grid.add(ranked, 1, 6);
        grid.add(new Label("Division:"), 0, 7);
        grid.add(division, 1, 7);

        ButtonBar.setType(actionAdd, ButtonType.OK_DONE);

        dlg.setMasthead("Add a game");
        dlg.setContent(grid);
        dlg.resizableProperty().set(false);
        dlg.getActions().addAll(actionAdd, Dialog.Actions.CANCEL);
        dlg.show();
    }

    @FXML
    private void applyFilter() {
        int numberWin = 0;
        int numberDef = 0;
        allGamesFiltered.clear();
        System.out.println(choiceChampion.getSelectionModel().getSelectedItem());
        if (!"All Champions".equals(choiceChampion.getSelectionModel().getSelectedItem())) {
            System.out.println("Filter for special champ");
            // FILTER FOR SPECIAL CHAMP
            if (choiceRank.getSelectionModel().getSelectedIndex() != 0) {
                if (choiceDivision.isVisible()) {
                    if (choiceDivision.getSelectionModel().getSelectedIndex() != 0) {
                        System.out.println("FILTER FOR SPECIAL CHAMP & SPECIAL RANK & SPECIAL DIVISION");
                        // FILTER FOR SPECIAL CHAMP & SPECIAL RANK & SPECIAL DIVISION
                        for (Game game : allGames) {
                            if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue() && game.getDivision() == choiceDivision.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                                numberWin = numberWin + 1;
                                allGamesFiltered.add(game);
                            } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue() && game.getDivision() == choiceDivision.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                                numberDef = numberDef + 1;
                                allGamesFiltered.add(game);
                            }

                        }
                        setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                    } else {
                        System.out.println("FILTER FOR SPECIAL CHAMP & SPECIAL RANK & ALL DIVISION");
                        // FILTER FOR SPECIAL CHAMP & SPECIAL RANK & ALL DIVISION
                        for (Game game : allGames) {
                            if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                                numberWin = numberWin + 1;
                                allGamesFiltered.add(game);
                            } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                                numberDef = numberDef + 1;
                                allGamesFiltered.add(game);
                            }

                        }
                        setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                    }
                } else {
                    System.out.println("FILTER FOR SPECIAL CHAMP & SPECIAL RANK");
                    // FILTER FOR SPECIAL CHAMP & SPECIAL RANK
                    for (Game game : allGames) {
                        if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                            numberWin = numberWin + 1;
                            allGamesFiltered.add(game);
                        } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue() && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                            numberDef = numberDef + 1;
                            allGamesFiltered.add(game);
                        }

                    }
                    setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                }
            } else {
                System.out.println("FILTER FOR SPECIAL CHAMP & ALL RANK");
                // FILTER FOR SPECIAL CHAMP & ALL RANK
                for (Game game : allGames) {
                    if (game.getResult() == Result.Victory && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                        numberWin = numberWin + 1;
                        allGamesFiltered.add(game);
                    } else if (game.getResult() == Result.Defeat && game.getChampion() == Champion.valueOf(choiceChampion.getValue())) {
                        numberDef = numberDef + 1;
                        allGamesFiltered.add(game);
                    }

                }
                setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
            }
        } else {
            System.out.println("Filter for all champ");
            if (choiceRank.getSelectionModel().getSelectedIndex() != 0) {
                if (choiceDivision.isVisible()) {
                    if (choiceDivision.getSelectionModel().getSelectedIndex() != 0) {
                        System.out.println("FILTER FOR ALL CHAMP & SPECIAL RANK & SPECIAL DIVISION");
                        // FILTER FOR ALL CHAMP & SPECIAL RANK & SPECIAL DIVISION
                        for (Game game : allGames) {
                            if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue() && game.getDivision() == choiceDivision.getValue()) {
                                numberWin = numberWin + 1;
                                allGamesFiltered.add(game);
                            } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue() && game.getDivision() == choiceDivision.getValue()) {
                                numberDef = numberDef + 1;
                                allGamesFiltered.add(game);
                            }

                        }
                        setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                    } else {
                        System.out.println("FILTER FOR ALL CHAMP & SPECIAL RANK & ALL DIVISION");
                        System.out.println(choiceRank.getValue());
                        // FILTER FOR ALL CHAMP & SPECIAL RANK & ALL DIVISION
                        for (Game game : allGames) {
                            if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue()) {
                                numberWin = numberWin + 1;
                                allGamesFiltered.add(game);
                            } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue()) {
                                numberDef = numberDef + 1;
                                allGamesFiltered.add(game);
                            }

                        }
                        setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                    }
                } else {
                    System.out.println("FILTER FOR ALL CHAMP & SPECIAL RANK");
                    // FILTER FOR ALL CHAMP & SPECIAL RANK
                    for (Game game : allGames) {
                        if (game.getResult() == Result.Victory && game.getRank() == choiceRank.getValue()) {
                            numberWin = numberWin + 1;
                            allGamesFiltered.add(game);
                        } else if (game.getResult() == Result.Defeat && game.getRank() == choiceRank.getValue()) {
                            numberDef = numberDef + 1;
                            allGamesFiltered.add(game);
                        }

                    }
                    setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
                }
            } else {
                System.out.println("FILTER FOR ALL CHAMP & ALL RANK");
                // FILTER FOR ALL CHAMP & ALL RANK
                for (Game game : allGames) {
                    if (game.getResult() == Result.Victory) {
                        numberWin = numberWin + 1;
                        allGamesFiltered.add(game);
                    } else {
                        numberDef = numberDef + 1;
                        allGamesFiltered.add(game);
                    }

                }
                setPieChartWinLoose(numberWin, numberDef, numberWin + " Win / " + numberDef + " Loose");
            }
        }
        addGamesToTableView();
        filterSelectedItem();
    }

    private void addGamesToTableView() {
        ObservableList<Game> gameData = FXCollections.observableArrayList();
        gameData.addAll(allGamesFiltered);
        tableView.getItems().clear();
        tableView.setItems(gameData);
    }

    private void filterSelectedItem() {
        ObservableList<Game> gameSelected;
        double gameSelectedKda = 0;
        double gameSelectedKill = 0;
        double gameSelectedDeath = 0;
        double gameSelectedAssist = 0;
        int gameSelectedVictory = 0;
        int gameSelectedDefeat = 0;

        if (tableView.getSelectionModel().getSelectedItems().size() == 0) {
            gameSelected = FXCollections.observableArrayList(allGamesFiltered);
        } else {
            gameSelected = tableView.getSelectionModel().getSelectedItems();

        }

        for (Game game : gameSelected) {
            gameSelectedKill = gameSelectedKill + game.getKill();
            gameSelectedDeath = gameSelectedDeath + game.getDeath();
            gameSelectedAssist = gameSelectedAssist + game.getAssist();

            if (game.getResult() == Result.Victory) {
                gameSelectedVictory = gameSelectedVictory + 1;
            } else {
                gameSelectedDefeat = gameSelectedDefeat + 1;
            }
        }

        if (gameSelectedDeath != 0) {
            gameSelectedKda = (gameSelectedKill + gameSelectedAssist) / gameSelectedDeath;
            averageKdaSelected.setText(String.valueOf(gameSelectedKda));
        } else {
            averageKdaSelected.setText("Infinite");
        }

        // Set PIECHART Data
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("Victory", gameSelectedVictory),
                        new PieChart.Data("Defeat", gameSelectedDefeat));
        pieChartSelected.setTitle(gameSelectedVictory + " Win(s) / " + gameSelectedDefeat + " Loose(s)");
        pieChartSelected.setData(pieChartData);
        pieChartSelected.setLabelLineLength(2);
        pieChartSelected.setLegendSide(Side.LEFT);

        applyCustomColorSequence(
                pieChartData,
                "green",
                "red"
        );

        // Set BARCHART Data
        barChartSelected.setTitle("K.D.A");

        barChartSelected.getData().clear();

        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();

        series1.getData().add(new XYChart.Data("Kill", gameSelectedKill));
        series2.getData().add(new XYChart.Data("Death", gameSelectedDeath));
        series3.getData().add(new XYChart.Data("Assist", gameSelectedAssist));

        barChartSelected.getData().addAll(series1, series2, series3);
    }
}
