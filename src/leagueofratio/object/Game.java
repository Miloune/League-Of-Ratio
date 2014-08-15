/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leagueofratio.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Miloune
 */
public class Game implements Serializable {

    static String path = "savedGames/";
    
    private final Result result;
    private final double kill;
    private final double death;
    private final double assist;
    private final double kda;
    private final int elo;
    private final Rank rank;
    private final Division division;
    private final Champion champion;

    public Game(Champion champion, Result result, double kill, double death, double assist, int elo, Rank rank, Division division) {
        this.result = result;
        this.kill = kill;
        this.death = death;
        this.assist = assist;
        this.elo = elo;
        this.rank = rank;
        this.champion = champion;
        
        if(rank.CHALLENGER == rank)
            this.division = Division.I;
        else
            this.division = division; 
        
        if(death != 0)
            this.kda = (kill + assist)/death;
        else
            this.kda = -1;
    }

    public double getKda() {
        return kda;
    }

    public Result getResult() {
        return result;
    }

    public double getKill() {
        return kill;
    }

    public double getDeath() {
        return death;
    }

    public double getAssist() {
        return assist;
    }

    public int getElo() {
        return elo;
    }

    public Rank getRank() {
        return rank;
    }

    public Division getDivision() {
        return division;
    }
    
    public Champion getChampion() {
        return champion;
    }

    /**
     * Serialize object type game
     *
     * @param game
     * @return path & name of the file created
     * @throws IOException
     */
    public static String saveGame(Game game) throws IOException {
        Date dNow = new Date();
        SimpleDateFormat ft
                = new SimpleDateFormat("dd.MM.yyyy'-'hh:mm:ss");

        String nameFile = "game" + ft.format(dNow) + ".lor";

        File file = new File(path + nameFile);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(game);

        return nameFile;
    }

    /**
     * Deserialize object type game
     * @param nameFile
     * @return the game loaded
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static Game loadGame(String nameFile) throws IOException, ClassNotFoundException {
        File file = new File(path + nameFile);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Game game = (Game) ois.readObject();
        
        return game;
    }
    
    public static ArrayList<Game> getAllGames() throws IOException, ClassNotFoundException {
        ArrayList<Game> gamesList = new ArrayList<>();
        
        File[] files; 
        File directoryToScan = new File(path); 
        files = directoryToScan.listFiles();
        
        for (File file : files) {
            gamesList.add(Game.loadGame(file.getName()));
        }
        
        return gamesList;
    }
}
