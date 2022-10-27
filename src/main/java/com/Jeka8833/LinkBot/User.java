package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;

import java.io.ByteArrayOutputStream;

public class User {

    public final Long chatId;
    public byte notification;
    public final boolean isAdmin;
    public String skipLesson;

    public User(Long chatId, byte isNotification, boolean isAdmin, String skipLesson) {
        this.chatId = chatId;
        this.notification = isNotification;
        this.isAdmin = isAdmin;
        this.skipLesson = skipLesson;
    }

    public boolean isSkipLesson(final int index) {
        final byte[] val = Util.hexStringToByteArray(skipLesson);
        for (byte b : val)
            if (b == index)
                return true;
        return false;
    }

    public boolean addSkip(final int index) {
        for (Lesson lesson : KPI.lessons) {
            if (lesson.lesson_id == index && lesson.choice) {
                final byte[] val = Util.hexStringToByteArray(skipLesson);
                for (byte b : val)
                    if (b == index)
                        return false;
                final byte[] out = new byte[val.length + 1];
                System.arraycopy(val, 0, out, 0, val.length);
                out[val.length] = (byte) index;
                skipLesson = Util.bytesToHex(out);
                LinkBotDB.write(LinkBotDB.Table.NOTIFICATION);
                return true;
            }
        }
        return false;
    }

    public boolean removeSkip(final int index) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        final byte[] val = Util.hexStringToByteArray(skipLesson);
        for (byte b : val) {
            if (b == index)
                continue;
            byteArray.write(b);
        }
        final byte[] out = byteArray.toByteArray();
        if (out.length == val.length)
            return false;
        skipLesson = Util.bytesToHex(out);
        LinkBotDB.write(LinkBotDB.Table.NOTIFICATION);
        return true;
    }

    public void resetSkip() {
        skipLesson = "";
        LinkBotDB.write(LinkBotDB.Table.NOTIFICATION);
    }
}
