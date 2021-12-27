package me.tinytank800.tinylives.utilities;

import me.tinytank800.tinylives.tinylives;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    public void createTable(){
        PreparedStatement ps;
        try{
            ps = tinylives.getInstance().SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS playerdata "
                    + "(UUID VARCHAR(36),NAME VARCHAR(100), LIVES INT(100), PRIMARY KEY (NAME))");
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player){
        try{
            UUID uuid = player.getUniqueId();
            if(!exists(uuid)){
                PreparedStatement ps2 = tinylives.getInstance().SQL.getConnection().prepareStatement("INSERT IGNORE INTO playerdata (NAME,UUID) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid){
        try {
            PreparedStatement ps = tinylives.getInstance().SQL.getConnection().prepareStatement("SELECT * FROM playerdata WHERE UUID=?");
            ps.setString(1,uuid.toString());
            ResultSet results = ps.executeQuery();
            if(results.next()){
                return true;
            }
            return false;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void addLives(UUID uuid, int lives){
        try{
            PreparedStatement ps = tinylives.getInstance().SQL.getConnection().prepareStatement("UPDATE playerdata SET LIVES=? WHERE UUID=?");
            ps.setInt(1,getLives(uuid) + lives);
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getLives(UUID uuid){
        try{
            PreparedStatement ps = tinylives.getInstance().SQL.getConnection().prepareStatement("SELECT LIVES FROM playerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int lives = 0;
            if(rs.next()){
                lives = rs.getInt("LIVES");
                return lives;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void emptyTable(){ //CLEAR TABLE OF ALL DATA
        try {
            PreparedStatement ps = tinylives.getInstance().SQL.getConnection().prepareStatement("TRUNCATE playerdata");
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void remove(UUID uuid){ //REMOVE PLAYER FROM DATABASE
        try {
            PreparedStatement ps = tinylives.getInstance().SQL.getConnection().prepareStatement("DELETE FROM playerdata WHERE UUID=?");
            ps.setString(1,uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
