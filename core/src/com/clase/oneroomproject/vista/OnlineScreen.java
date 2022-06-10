package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class OnlineScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private Skin skin;
    /**
     * Campo de texto donde escribes el ID
     */
    private TextField txtID;

    public OnlineScreen(MainGame game){
        this.game=game;
    }

    @Override
    public void show() {
        initComponentes();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
    private void initComponentes(){
        skin = new Skin(Gdx.files.internal("C:\\Users\\KingAlfy\\IdeaProjects\\OneRoomProject\\assets\\pruebaSkin\\uiskin.json"));
        stage = new Stage();
        txtID = new TextField("Introduce el ID del jugador:", skin);

    }
}
