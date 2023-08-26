package com.Jeka8833.LinkBot.dataBase;

import com.Jeka8833.LinkBot.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.*;

public class LinkBotDB {

    private static final Logger LOGGER = LogManager.getLogger(LinkBotDB.class);

    public static Integer shiftWeek = 0;
    public static Integer onNotification = 0;
    public static final Map<Integer, String> urls = new HashMap<>();
    public static final List<User> users = new ArrayList<>();

    public static void read() {
        DatabaseManager.db.checkConnect();

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Setting\"")) {
            while (resultSet.next()) {
                switch (resultSet.getString(1)) {
                    case "weekShift" -> shiftWeek = resultSet.getInt(2);
                    case "notification" -> onNotification = resultSet.getInt(2);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fail read settings:", e);
        }

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Links\"")) {
            while (resultSet.next()) {
                urls.put(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (Exception e) {
            LOGGER.error("Fail read links:", e);
        }

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Users\"")) {
            users.clear();
            while (resultSet.next()) {
                users.add(new User(resultSet.getLong(1), resultSet.getByte(2),
                        resultSet.getBoolean(3), resultSet.getString(4)));
            }
        } catch (Exception e) {
            LOGGER.error("Fail read users:", e);
        }
    }

    public static void write(final Table table) {
        DatabaseManager.db.checkConnect();
        switch (table) {
            case SETTING -> {
                try {
                    DatabaseManager.db.statement.executeUpdate("INSERT INTO \"LB_Setting\" (\"Name\", \"Value\") " +
                            "VALUES ('weekShift', " + shiftWeek + "), ('notification', " + onNotification + ") " +
                            "ON CONFLICT (\"Name\") DO UPDATE SET \"Value\" = EXCLUDED.\"Value\"");
                } catch (Exception e) {
                    LOGGER.error("Fail write settings:", e);
                }
            }
            case LINK -> {
                try {
                    var sqlRequest = new StringJoiner(",",
                            "INSERT INTO \"LB_Links\" (\"id\", \"link\") VALUES ",
                            "ON CONFLICT (\"id\") DO UPDATE SET \"link\" = EXCLUDED.\"link\"");
                    for (Map.Entry<Integer, String> entry : urls.entrySet())
                        sqlRequest.add("(" + entry.getKey() + ",'" + entry.getValue() + "')");

                    DatabaseManager.db.statement.executeUpdate(sqlRequest.toString());
                } catch (Exception e) {
                    LOGGER.error("Fail write links:", e);
                }
            }
            case NOTIFICATION -> {
                try {
                    var sqlRequest = new StringJoiner(",", "INSERT INTO \"LB_Users\" " +
                            "(\"id\", \"timeNotification\", \"isAdmin\", \"skipLesson\") VALUES ",
                            "ON CONFLICT (\"id\") DO UPDATE SET \"timeNotification\" = EXCLUDED.\"timeNotification\"," +
                                    " \"skipLesson\" = EXCLUDED.\"skipLesson\"");
                    for (User user : users) {
                        sqlRequest.add("(" + user.chatId + "," + user.notification + "," +
                                user.isAdmin + ",'" + user.skipLesson + "')");
                    }

                    DatabaseManager.db.statement.executeUpdate(sqlRequest.toString());
                } catch (Exception e) {
                    LOGGER.error("Fail write users:", e);
                }
            }
        }
    }

    public enum Table {
        LINK,
        NOTIFICATION,
        SETTING
    }
}
