package application;

import java.io.File;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController implements Initializable {

	// Table View
	@FXML
	private TableView<Dep> TV;

	@FXML
	private TableColumn<Dep,Double> NC;

	@FXML
	private TableColumn<Dep,Double> VC;

	@FXML
	private TableColumn<Dep,Double> SC;

	@FXML
	private TableColumn<Dep,Double> EC;


	// Text

	@FXML
	private TextField DST;


	@FXML
	private TextField DSupT;

	@FXML
	private TextField DAT;

	@FXML
	private TextField DHT;

	//Button
	@FXML
	private Button AB;

	@FXML
	private Button RB;

	@FXML
	private Button MB;

	@FXML
	private Button FB2;

	@FXML
	private Button FB;

	@FXML
	private Button EB;

	//Placer les depenses dans une observable list

	public ObservableList<Dep> DepData= FXCollections.observableArrayList(); 

	//Une méthode pour accéder la liste des depenses

	public ObservableList<Dep> getDepData()
	{
		return DepData;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) 

	{
		// Pour attribuer les valeurs aux colonnes du TableView

		NC.setCellValueFactory(new PropertyValueFactory <> ("Nourriture"));
		VC.setCellValueFactory(new PropertyValueFactory <> ("Habillement"));
		EC.setCellValueFactory(new PropertyValueFactory <> ("Extra"));
		SC.setCellValueFactory(new PropertyValueFactory <> ("Service"));

		// Bouttons boolean true:

		MB.setDisable(true);
		EB.setDisable(true);
		RB.setDisable(true);

		//Tableau
		TV.setItems(DepData);
		showDep(null);
		TV.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)-> showDep(newValue));

	}


	//Effacer le contenu des champs
	@FXML
	void clearFields()
	{
		DAT.setText("");
		DHT.setText("");
		DST.setText("");
		DSupT.setText("");

	}

	//Ajouter une depense
	@FXML
	void ajouter() 

	{
		Dep tmp = new Dep();
		tmp=new Dep();
		tmp.setNC(Double.parseDouble(DAT.getText()));
		tmp.setVC(Double.parseDouble(DHT.getText()));
		tmp.setEC(Double.parseDouble(DSupT.getText()));
		tmp.setSC(Double.parseDouble(DST.getText()));
		DepData.add(tmp);
		clearFields();
	}

	//Afficher les depense
	@FXML
	void showDep(Dep dep)
	{
		if(dep !=null)
		{
			DAT.setText(Double.toString(dep.getNC()));
			DHT.setText(Double.toString(dep.getVC()));
			DST.setText(Double.toString(dep.getSC()));
			DSupT.setText(Double.toString(dep.getEC()));

			// Bouttons boolean false:
			MB.setDisable(false);
			EB.setDisable(false);
			RB.setDisable(false);
		}
		else
		{
			clearFields();
		}

	}
	//Modification d'une depense
	@FXML
	void ModifierDep()
	{
		Dep dep=	TV.getSelectionModel().getSelectedItem();
		dep.setNC(Double.parseDouble(DAT.getText()));
		dep.setVC(Double.parseDouble(DHT.getText()));
		dep.setSC(Double.parseDouble(DST.getText()));
		dep.setEC(Double.parseDouble(DSupT.getText()));
		TV.refresh();
	}

	//Effacer une depense
	@FXML
	void clearDep() 
	{
		int selectedIndex= TV.getSelectionModel().getSelectedIndex();
		if(selectedIndex >=0)
		{
			Alert alert=new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Effacer");
			alert.setContentText("Confirmer la suppression!");
			Optional<ButtonType> result=alert.showAndWait();
			if(result.get()==ButtonType.OK)
				TV.getItems().remove(selectedIndex);
		}

	}

	//Sauvegarde de donner:
	public File getDepFilePath()

	{
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		String filePath = prefs.get("filePath", null);

		if( filePath !=null)
		{
			return new File(filePath);
		}
		else
		{
			return null;
		}
	}

	//Attribuer un chemin de fichiers:
	public void setDepFilePath(File file)
	{
		Preferences prefs=Preferences.userNodeForPackage(Main.class);
		if(file !=null)
		{
			prefs.put("filePath", file.getPath());
		}
		else
		{
			prefs.remove("filePath");
		}
	}

	//Convertir les donner en type JavaFX
	public void loodDepDataFromFile(File file)
	{
		try {
			JAXBContext context = JAXBContext.newInstance(DepListWrapper.class);
			Unmarshaller um =context.createUnmarshaller();

			DepListWrapper wrapper = (DepListWrapper)um.unmarshal(file);
			DepData.clear();
			DepData.addAll(wrapper.getDep());

			Stage pStage=(Stage) TV.getScene().getWindow();
			pStage.setTitle(file.getName());

			setDepFilePath(file);
		}catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Les donnees n'ont pas ete trouver");
			alert.setContentText("Les donnees ne pouvaient pas etre trouver dans le fichier :\n"+file.getPath());
			alert.showAndWait();
		}
	}

	//Convertir les donnee en type XML

	public void saveDepDataToFile(File file) 
	{
		try {
			JAXBContext context = JAXBContext.newInstance(DepListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
			DepListWrapper wrapper = new DepListWrapper();
			wrapper.setDep(DepData);

			m.marshal(wrapper,  file);
			//Sauvgarde dans le registre
			setDepFilePath(file);
		}catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Donnees non sauvegardees");
			alert.setContentText("Les donnees ne pouvaient pas etre sauvegardees dans le fichier:\n"+file.getPath());
			alert.showAndWait();

		}
	}

	//Commencer un nouveau
	@FXML
	private void handleNew()
	{
		getDepData().clear();
		setDepFilePath(null);
	}

	//FileChooser - Permettre de choisir un fichier
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Filtre l'extension du fichier a chercher
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		//Montrer le dialogue
		File file = fileChooser.showOpenDialog(null);

		if(file !=null) {
			loodDepDataFromFile(file);
		}
	}

	//Sauvegarde le fichier correspondant
	@FXML
	private void handleSave() 
	{
		File etudiantFile = getDepFilePath();

		if(etudiantFile !=null) {
			saveDepDataToFile(etudiantFile);
		} else 
		{
			handleSaveAs();
		}
	}

	//Ouvrire le FileChooser pour chemin
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Sauvegarde

		File file = fileChooser.showSaveDialog(null);

		if (file !=null);{
			//Verification
			if( !file.getPath().endsWith(".xml")) {
				file = new File(file.getPath()+".xml");
			}
			saveDepDataToFile(file);		
		}
	}

	//Trouver les input non numerique
	@FXML
	public void verifNum() 
	{
		DAT.textProperty().addListener((observable,oldValue,newValue)->{
			
			if(!newValue.matches("^[0-9](\\.[0-9]+)?&&")) {
				DAT.setText(newValue.replaceAll("[^\\d*]", ""));
			}
		});

		DHT.textProperty().addListener((observable,oldValue,newValue)->{
		 
			if(!newValue.matches("^[0-9](\\.[0-9]+)?&&")) {
				DHT.setText(newValue.replaceAll("[^\\d*]", ""));
			}
		});

		DST.textProperty().addListener((observable,oldValue,newValue)->{
			
			if(!newValue.matches("^[0-9](\\.[0-9]+)?&&")) {
				DST.setText(newValue.replaceAll("[^\\d*]", ""));
			}
		});

		DSupT.textProperty().addListener((observable,oldValue,newValue)->{
			
			if(!newValue.matches("^[0-9](\\.[0-9]+)?&&")) {
				DSupT.setText(newValue.replaceAll("[^\\d*]", ""));
			}
		});

	}

	// Verifier champs vides
	private boolean noEmptyInput() {
		String errorMessage ="";

		if(DAT.getText()==null||DAT.getText().length()==0) {
			errorMessage+="Le champs Nourriture ne doit pas etre vide! \n";
		}
		if(DHT.getText()==null||DHT.getText().length()==0) {
			errorMessage+="Le champs Habillement ne doit pas etre vide! \n";
		}
		if(DST.getText()==null||DST.getText().length()==0) {
			errorMessage+="Le champs Service ne doit pas etre vide! \n";
		}
		if(DSupT.getText()==null||DSupT.getText().length()==0) {
			errorMessage+="Le champs Extra ne doit pas etre vide! \n";
		}
		if(errorMessage.length()==0) {
			return true;
		}
		else {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Champs manquants");
			alert.setHeaderText("Completer les champs manquants");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
	}
		
		//Button de sortie:
		   
	    @FXML
	   private void Fermer()
	   
	    {
	    	Alert alert=new Alert(AlertType.CONFIRMATION);//Confirmation pour quitter
	    	alert.setHeaderText("Confirmation");
	    	alert.setTitle("Avertissement");
	    	alert.setContentText("Etes-vous sûr de vouloir quitter l'application?");
	    	Optional<ButtonType> result=alert.showAndWait();
	    	if(result.get()==ButtonType.OK)
	    	{
	    	System.exit(0);
	    	}
			 

	}

}

