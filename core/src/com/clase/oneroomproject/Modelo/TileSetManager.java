package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.HashMap;

/**
 * Clase encargada de cargar las texturas y los assets y gestionarlos.
 */
public class TileSetManager
{
    public TileSetManager()
    {
        tileSetsMap = new HashMap<>();

        //testSet = new TiledMapTileSet();
    }

    /**
     * Carga los Tiles de un TileSet dada su textura.
     * @param path Ubicación de la textura.
     * @param tileSetName Nombre del TileSet.
     * @param tileWidth Anchura en pixeles de las Tiles.
     * @param tileHeight Altura en píxeles de las Tiles.
     * @return True si se ha cargado correctamente. False si ha ocurrido algún error.
     */
    public boolean loadTileSet(String path, String tileSetName,
                               //int numTilesX, int numTilesY,
                               int tileWidth, int tileHeight)
    {
        Gdx.app.log("TileSetManager", "Cargando el tileSet de: " + path);
        //Cargamos la textura indicada.
        Texture txLoader = new Texture(Gdx.files.internal(path));

        //Dividimos la textura en cada uno de sus tiles.
        TextureRegion[][] splitedTiles = TextureRegion.split(txLoader, tileWidth, tileHeight);

        TiledMapTileSet tileSet = new TiledMapTileSet();

        //Añadimos al tileSet todas las tiles que hemos partido.
        int idCounter = 0;
        for(int y = 0; y < splitedTiles.length; y++)
        {
            for(int x = 0; x < splitedTiles[y].length; x++)
            {
                StaticTiledMapTile tile = new StaticTiledMapTile(splitedTiles[y][x]);
                tileSet.putTile(idCounter, tile);
                idCounter++;
            }
        }
        Gdx.app.log("TileSetManager", "Se han cargado " + idCounter +
                " tiles en el tileSet: " + tileSetName);

        tileSetsMap.put(tileSetName, tileSet);

        return true;
    }

    /**
     * Devuelve el TileSet con el nombre indicado.
     * @param tileSetName Nombre del TileSet a obtener.
     * @return TileSet con el nombre indicado.
     */
    public TiledMapTileSet GetTileSet(String tileSetName)
    {
        TiledMapTileSet set = tileSetsMap.get(tileSetName);
        if(set == null)
        {
            Gdx.app.error("TileSetManager", "No se ha encontrado el tileSet con nombre: " + tileSetName);
        }
        return set;
    }

    //TiledMapTileSet testSet;

    HashMap<String, TiledMapTileSet> tileSetsMap;

}


