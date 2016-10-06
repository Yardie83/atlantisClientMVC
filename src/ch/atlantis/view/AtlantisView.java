package ch.atlantis.view;

import ch.atlantis.util.Language;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 17.07.2016.
 * <p>
 * This class acts as a hub from where the different views,
 * like the GameLobby, the Options etc. get instantiated.
 */

public class AtlantisView {

    private IntroView introView;
    private Stage introStage;

    private GameLobbyView gameLobbyView;

    private CreateGameView createGameView;
    private Stage createGameStage;

    private LoginView loginView;
    private Stage loginStage;

    private NewProfileView newProfileView;
    private Stage profileStage;

    private OptionsView optionsView;
    private Stage optionsStage;

    private SimpleIntegerProperty height;
    private SimpleIntegerProperty width;

    private ArrayList<Control> controls;

    private boolean fullscreen;
    private Stage activeOverlayStage;

    private Language selectedLanguage;


    public AtlantisView( Stage introStage ) {

        this.introStage = introStage;
        width = new SimpleIntegerProperty( 1280 );
        height = new SimpleIntegerProperty( 800 );

        controls = new ArrayList<>();
    }

    /**
     * Creates the Intro view
     * <p>
     * Hermann Grieder
     */

    public void createIntroView() {
        this.introView = new IntroView( introStage );
    }

    /**
     * Creates the GameLobby view
     * <p>
     * Hermann Grieder
     *
     * @param fullscreen Shows the GameLobby in fullscreen if true
     */

    public void createGameLobbyView( Boolean fullscreen ) {

        this.fullscreen = fullscreen;

        if ( gameLobbyView == null ) {

            if ( fullscreen ) {
                width.setValue( Screen.getPrimary().getBounds().getWidth() );
                height.setValue( Screen.getPrimary().getBounds().getHeight() );
                this.gameLobbyView = new GameLobbyView( height.getValue(), width.getValue(), true );
                //ScenicView.show(gameLobbyView);
            } else {
                this.gameLobbyView = new GameLobbyView( height.getValue(), width.getValue(), false );
                bindSizeToStage();
            }
        }

        getControls( this.gameLobbyView );
        setControlText( controls );
    }

    /**
     * Binds the width and the height to the GameLobby Stage to ensure that
     * the overlays have the same dimensions as the game lobby stage when the
     * user adjust the windows size. Otherwise the overlays have the dimensions
     * of the first instantiation of the GameLobby and would either be to small
     * or to big.
     * <p>
     * Hermann Grieder
     */

    //Workaround: There seems to be a padding or margin of sorts on the stage,
    //that's why we subtract a couple pixels of the width and the height.
    public void bindSizeToStage() {
        width.bind( gameLobbyView.getGameLobbyStage().widthProperty().subtract( 8 ) );
        height.bind( gameLobbyView.getGameLobbyStage().heightProperty().subtract( 8 ) );
    }

    /**
     * Creates the Create Game view
     * <p>
     * Hermann Grieder
     */

    public void createCreateGameView( Stage parentStage ) {
        if ( this.createGameView == null ) {
            this.createGameView = new CreateGameView( height.getValue(), width.getValue() );
            new Scene( createGameView );
        }
        createGameStage = new Stage();
        setupOverlay( createGameStage, createGameView.getScene(), parentStage, "css_CreateGameView" );
        activeOverlayStage = createGameStage;
    }

    /**
     * Creates the Login view
     * <p>
     * Hermann Grieder
     */
    public void createLoginView( Stage parentStage ) {
        if ( this.loginView == null ) {
            this.loginView = new LoginView( height.getValue(), width.getValue() );
            new Scene( loginView );
        }
        loginStage = new Stage();
        setupOverlay( loginStage, loginView.getScene(), parentStage, "css_LoginView" );
        activeOverlayStage = loginStage;
    }

    /**
     * Creates the New Profile view
     * <p>
     * Hermann Grieder
     */
    public void createNewProfileView( Stage parentStage ) {
        if ( this.newProfileView == null ) {
            this.newProfileView = new NewProfileView( height.getValue(), width.getValue() );
            new Scene( newProfileView );
        }
        getControls( this.newProfileView );
        setControlText( controls );
        profileStage = new Stage();
        setupOverlay( profileStage, newProfileView.getScene(), parentStage, "css_NewProfileView" );
        activeOverlayStage = profileStage;
    }

    /**
     * Creates the Options view
     * <p>
     * Hermann Grieder
     */

    public void createOptionsView( ArrayList<Language> languageList, String culture, Stage parentStage ) {
        if ( this.optionsView == null ) {
            this.optionsView = new OptionsView( height.getValue(), width.getValue(), languageList, culture );
            new Scene( optionsView );
        }
        optionsStage = new Stage();
        setupOverlay( optionsStage, optionsView.getScene(), parentStage, "css_OptionsView" );
        activeOverlayStage = optionsStage;
    }

    private void getControls( Pane pane ) {

        for ( Node node : pane.getChildren() ) {
            if ( node instanceof Pane ) {
                getControls( (Pane) node );
            } else if ( node instanceof Control ) {
                Control c = (Control) node;
                controls.add( c );
            }
        }
    }

    private void setControlText( ArrayList<Control> controls ) {

        for ( Control control : controls ) {

            if ( control instanceof Button ) {

                Button button = (Button) control;
                addLanguageTextToButtonControl( button );
            }

            if ( control instanceof Label ) {

                Label label = (Label) control;
                addLanguageTextToLabelControl( label );
            }
        }

        controls.clear();
    }

    private void addLanguageTextToButtonControl( Button button ) {

        if ( selectedLanguage != null ) {

            for ( String id : selectedLanguage.getLanguageTable().keySet() ) {

                if ( button.getId() != null ) {

                    if ( button.getId().equals( id ) ) {

                        //System.out.println("!!! LADIES AND GENTLEMEN WE HAVE A MATCH !!!");

                        //System.out.println("LE TEXT: " + language.getLanguageTable().get(id));

                        button.setText( selectedLanguage.getLanguageTable().get( id ) );
                    }
                }
            }
        }
    }

    private void addLanguageTextToLabelControl( Label label ) {

        if ( selectedLanguage != null ) {

            for ( String id : selectedLanguage.getLanguageTable().keySet() ) {

                if ( label.getId() != null ) {

                    if ( label.getId().equals( id ) ) {

                        //System.out.println("!!! LADIES AND GENTLEMEN WE HAVE A MATCH !!!");

                        //System.out.println("LE TEXT: " + language.getLanguageTable().get(id));

                        label.setText( selectedLanguage.getLanguageTable().get( id ) );
                    }
                }
            }
        }
    }

    /**
     * Sets the settings for the stage to be overlaid onto the parent stage.
     * Matches the dimensions and location of the overlay to the dimensions
     * of the parent stage.
     * <p>
     * Hermann Grieder
     *
     * @param overlayStage The stage of the overlay to set up
     */

    private void setupOverlay( Stage overlayStage, Scene scene, Stage parentStage, String cssString ) {

        if ( cssString != null ) {
            String css = this.getClass().getResource( "../res/css/" + cssString + ".css" ).toExternalForm();
            scene.getStylesheets().add( css );
        }
        // Make it so that the overlays block access to the parentStage
        overlayStage.initModality( Modality.WINDOW_MODAL );
        //Match the X and Y to the Game Lobby's X and Y coordinates
        overlayStage.setX( parentStage.getX() );
        overlayStage.setY( parentStage.getY() );
        //Set the dimensions of the Stage
        overlayStage.setMinHeight( parentStage.getHeight() );
        overlayStage.setMinWidth( parentStage.getWidth() );
        //Set opacity for the overlays
        overlayStage.opacityProperty().setValue( 0.95 );
        //Remove the Window decorations minimize, maximize and close button and the frame
        overlayStage.initStyle( StageStyle.TRANSPARENT );
        //Make it so that the overlays are always on top of the other windows
        overlayStage.setAlwaysOnTop( true );
    }

    public IntroView getIntroView() {
        return introView;
    }

    public Stage getIntroStage() {
        return introStage;
    }

    public GameLobbyView getGameLobbyView() {
        return gameLobbyView;
    }

    public CreateGameView getCreateGameView() {
        return createGameView;
    }

    public Stage getCreateGameStage() {
        return createGameStage;
    }

    public LoginView getLoginView() {
        return this.loginView;
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    public NewProfileView getNewProfileView() {
        return newProfileView;
    }

    public Stage getProfileStage() {
        return profileStage;
    }

    public OptionsView getOptionsView() {
        return optionsView;
    }

    public Stage getOptionsStage() {
        return optionsStage;
    }

    public SimpleIntegerProperty heightProperty() {
        return height;
    }

    public int getWidth() {
        return width.get();
    }

    public SimpleIntegerProperty widthProperty() {
        return width;
    }

    public void setWidth( int width ) {
        this.width.set( width );
    }

    public void setFullscreen( boolean fullscreen ) {
        this.fullscreen = fullscreen;
    }

    public boolean setSelectedLanguage( Language selectedLanguage ) {

        if ( selectedLanguage != null ) {

            this.selectedLanguage = selectedLanguage;
            return true;
        }
        return false;
    }

    public void closeActiveOverlay() {
        this.activeOverlayStage.close();
    }
}