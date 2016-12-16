/*
  Графическая оболочка программы
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GUI extends Application {
    private String inputFileName;
    private String inputFileDirectory;
    private String outputFileDirectory;
    private TextField inputFileTextField;
    private TextArea resultTextField;
    private CheckBox outputScreenCheckBox;
    private Convert convert;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        outputScreenCheckBox = new CheckBox("Показать на экране");
        outputScreenCheckBox.setVisible(false);

        CheckBox outputFileCheckBox = new CheckBox("Записать в файл");
        outputFileCheckBox.setVisible(false);

        primaryStage.setTitle("Meteo station");

        final VBox verticalBox = new VBox(10);
        verticalBox.setPadding(new Insets(10,10,10,10));
        verticalBox.setAlignment(Pos.CENTER);

        final HBox horizontalBox1 = new HBox(10);
        horizontalBox1.setAlignment(Pos.CENTER);
        final HBox horizontalBox2 = new HBox(10);
        horizontalBox2.setAlignment(Pos.CENTER);

        final Scene scene = new Scene(verticalBox,350,410);

        final FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        chooser.getExtensionFilters().add(filter);

        resultTextField = new TextArea();
        resultTextField.setPrefHeight(300);
        resultTextField.setPrefWidth(280);
        resultTextField.setPrefColumnCount(5);
        resultTextField.setVisible(false);

        final Label inputFileLabel = new Label("Файл с исходными данными:");
        inputFileTextField = new TextField();
        inputFileTextField.setEditable(false);
        inputFileTextField.setPromptText("inputFile");

        final Button saveButton = new Button("Save");
        saveButton.setDisable(true);
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
                                   @Override
                                   public void handle(ActionEvent event) {
                                       try {
                                           chooser.setTitle("Сохранить файл");
                                           File file = chooser.showSaveDialog(primaryStage);
                                           outputFileDirectory = file.getAbsolutePath();
                                           convert.saveFile(outputFileDirectory);
                                       } catch (Exception exc) {
                                           //
                                       }
                                   }
                               });

        final Button openButton = new Button("Open");
        openButton.setOnAction(new EventHandler<ActionEvent>() {
                                   @Override
                                   public void handle(ActionEvent event) {
                                       try {
                                           chooser.setTitle("Открыть файл");
                                           File file = chooser.showOpenDialog(primaryStage);
                                           inputFileName = file.getName();
                                           inputFileTextField.setText(inputFileName);
                                           inputFileDirectory = file.getAbsolutePath();
                                           convert = new Convert();
                                           outputScreenCheckBox.setVisible(true);
                                           saveButton.setDisable(false);
                                           if (outputScreenCheckBox.isSelected())
                                               resultTextField.setText(convert.sort(inputFileDirectory));
                                       } catch (NullPointerException exc) {
                                           inputFileTextField.setText("");
                                       }
                                   }
                               });

        Button startButton = new Button("Преобразовать");
        startButton.setVisible(false);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        if (inputFileTextField != null)
                                            resultTextField.setText(convert.sort(inputFileDirectory));
                                    }
                                });



        outputScreenCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                                             @Override
                                             public void handle(ActionEvent event) {
                                                 if (outputScreenCheckBox.isSelected()) {
                                                     resultTextField.setVisible(true);
                                                     resultTextField.setText(convert.sort(inputFileDirectory));
                                                 } else {
                                                     resultTextField.setVisible(false);
                                                 }
                                             }
                                         });

        horizontalBox1.getChildren().addAll(inputFileTextField,openButton,saveButton);
        horizontalBox2.getChildren().addAll(outputScreenCheckBox, outputFileCheckBox);
        verticalBox.getChildren().addAll(inputFileLabel,horizontalBox1,horizontalBox2,resultTextField);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
