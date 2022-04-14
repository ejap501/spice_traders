package com.mygdx.pirategame.save;

import com.mygdx.pirategame.Hud;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Used as a superclass for all loaders
 * loaders are classes used to load a game state
 * this class is used to handle saving and loading
 */
public abstract class SaveLoader {

    public abstract void load(GameScreen screen);

    public void save(GameScreen screen, File file) {

        try {
            if(!file.exists()){
                file.createNewFile();
            }

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("game");

            // saving player information
            Element player = document.createElement("player");
            screen.getPlayer().save(document, player);
            root.appendChild(player);

            // saving points / gold
            Element hud = document.createElement("hud");
            Hud.save(document, hud);
            root.appendChild(hud);



            document.appendChild(root);
            // creating the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(domSource, streamResult);

        } catch(Exception e) {
            // TODO save file is invalid, error message needs displaying
        }
        // TODO save to XML
    }

}
