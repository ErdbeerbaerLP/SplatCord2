package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

public class ImageNode extends LInk3Node  {
    public String image;

    public String getFullImageURL(){
        return "https://slushiegoose.github.io/"+image.replace("../","");
    }
}
