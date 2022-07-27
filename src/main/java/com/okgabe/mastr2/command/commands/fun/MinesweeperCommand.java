/*
 * Copyright 2020 Gabriel Keller
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License.
 * A copy of this license can be found at
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.
 */

package com.okgabe.mastr2.command.commands.fun;

import com.okgabe.mastr2.Mastr;
import com.okgabe.mastr2.command.CommandBase;
import com.okgabe.mastr2.command.CommandCategory;
import com.okgabe.mastr2.command.CommandEvent;
import com.okgabe.mastr2.event.ResponseListener;
import com.okgabe.mastr2.util.StringUtil;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.util.Arrays;
import java.util.function.Consumer;

public class MinesweeperCommand extends CommandBase {
    
    private static final int COLUMNS = 9;
    private static final int ROWS = 9;
    
    private CustomEmoji bombEmote;
    private CustomEmoji flagEmote;
    private CustomEmoji oneEmote;
    private CustomEmoji twoEmote;
    private CustomEmoji threeEmote;
    private CustomEmoji fourEmote;
    private CustomEmoji fiveEmote;
    private CustomEmoji sixEmote;
    private CustomEmoji sevenEmote;
    private CustomEmoji eightEmote;
    private CustomEmoji nineEmote;

    private CustomEmoji aEmote;
    private CustomEmoji bEmote;
    private CustomEmoji cEmote;
    private CustomEmoji dEmote;
    private CustomEmoji eEmote;
    private CustomEmoji fEmote;
    private CustomEmoji gEmote;
    private CustomEmoji hEmote;
    private CustomEmoji iEmote;

    private CustomEmoji blankTile;

    private String flag;
    private String bomb;

    public MinesweeperCommand(Mastr mastr) {
        super(mastr);
        this.command = "minesweeper";
        this.description = "Gives you a playable game of Minesweeper!";
        this.aliases = new String[] {"msw"};
        this.category = CommandCategory.FUN;
        this.syntax = new String[] {""};
        
        bombEmote = mastr.getShardManager().getEmojiById(746599421764173944L);
        flagEmote = mastr.getShardManager().getEmojiById(746601049854574694L);
        oneEmote = mastr.getShardManager().getEmojiById(746642432728236092L);
        twoEmote = mastr.getShardManager().getEmojiById(746642432690487307L);
        threeEmote = mastr.getShardManager().getEmojiById(746642432770179173L);
        fourEmote = mastr.getShardManager().getEmojiById(746642432732561508L);
        fiveEmote = mastr.getShardManager().getEmojiById(746642432522715178L);
        sixEmote = mastr.getShardManager().getEmojiById(746642432472514581L);
        sevenEmote = mastr.getShardManager().getEmojiById(746642432342491210L);
        eightEmote = mastr.getShardManager().getEmojiById(746642432736755752L);
        nineEmote = mastr.getShardManager().getEmojiById(746642432745013308L);

        aEmote = mastr.getShardManager().getEmojiById(746645135760293908L);
        bEmote = mastr.getShardManager().getEmojiById(746645135671951401L);
        cEmote = mastr.getShardManager().getEmojiById(746645135412166748L);
        dEmote = mastr.getShardManager().getEmojiById(746645135688728606L);
        eEmote = mastr.getShardManager().getEmojiById(746645135630008360L);
        fEmote = mastr.getShardManager().getEmojiById(746645135592521769L);
        gEmote = mastr.getShardManager().getEmojiById(746645135609036841L);
        hEmote = mastr.getShardManager().getEmojiById(746645135328018463L);
        iEmote = mastr.getShardManager().getEmojiById(746645135663562782L);

        blankTile = mastr.getShardManager().getEmojiById(746647537913561128L);

        flag = flagEmote.getAsMention();
        bomb = bombEmote.getAsMention();
    }

    @Override
    public boolean called(CommandEvent e) {
        return true;
    }

    @Override
    public void execute(CommandEvent e) {
        String[][] minesweeperOriginal = generateMinesweeper(10);
        String[][] minesweeperCopy = copy(minesweeperOriginal);
        String minesweeperString = minesweeperToString(minesweeperOriginal);

        e.getChannel().sendMessage(minesweeperString).queue(minesweeperMessage -> {
            e.getChannel().sendMessage(e.getAuthor().getAsMention() + ", you have started a game of Minesweeper! Here are some commands you can use:\n" +
                    "flag <tile>, reveal, end").queue(actionMessage -> {

                MinesweeperResponseListener responseListener = new MinesweeperResponseListener(e.getChannel().getType(), e.getChannel().getIdLong(), e.getBotUser().getUserId(), 10 * 60, ident -> {
                    MinesweeperResponseListener identity = (MinesweeperResponseListener) ident;
                    String content = ident.getMessage().getContentRaw().toLowerCase();
                    if (content.startsWith("f")) {
                        if (!content.contains(" ")) return; // false message / no flag set

                        ident.getMessage().delete().queue();
                        identity.setFailedAttempt(false);
                        String tileCode = content.split(" ")[1];
                        char firstChar = tileCode.charAt(0);
                        char secondChar = tileCode.charAt(1);
                        int row;
                        int col;
                        boolean validTitle = true;

                        if (StringUtil.isNumeric(String.valueOf(firstChar))) {
                            row = Integer.parseInt(String.valueOf(firstChar)) - 1;
                            col = StringUtil.positionInAlphabet(secondChar);
                            if (col == -1) validTitle = false;
                        } else {
                            row = Integer.parseInt(String.valueOf(secondChar)) - 1;
                            col = StringUtil.positionInAlphabet(firstChar);
                            if (col == -1) validTitle = false;
                        }

                        if(!validTitle){
                            actionMessage.editMessage("You provided an invalid tile!").queue();
                            return;
                        }

                        String tile = minesweeperCopy[row][col];
                        if (tile.equals(flag)) {
                            tile = minesweeperOriginal[row][col];
                        } else {
                            tile = flagEmote.getAsMention();
                        }
                        minesweeperCopy[row][col] = tile;

                        minesweeperMessage.editMessage(minesweeperToString(minesweeperCopy)).queue();
                        actionMessage.editMessage("Set tile " + firstChar + secondChar + " to a flag!").queue();
                    } else if (content.startsWith("r")) {
                        ident.getMessage().delete().queue();
                        minesweeperMessage.editMessage(minesweeperString.replaceAll("\\|\\|", "")).queue(); // Remove all spoilers
                        actionMessage.editMessage("Revealed! Your game is over.").queue();
                        mastr.getResponseHandler().unregister(identity);
                    } else if (content.startsWith("e")) {
                        ident.getMessage().delete().queue();
                        actionMessage.editMessage("Ending your game of Minesweeper.").queue();
                        mastr.getResponseHandler().unregister(ident);
                    } else {
                        if (identity.isFailedAttempt()) {
                            actionMessage.editMessage("Ending your game of Minesweeper.").queue();
                            mastr.getResponseHandler().unregister(identity);
                        } else {
                            actionMessage.editMessage("I couldn't understand that. You can say flag, reveal, or end.").queue();
                            identity.setFailedAttempt(true);
                        }
                    }
                }, expiration -> actionMessage.editMessage("Your game of Minesweeper has expired.").queue());

                mastr.getResponseHandler().register(responseListener);
            });
        });
    }

    public String minesweeperToString(String[][] field){
        // Convert to disc message
        StringBuilder message = new StringBuilder();
        for(int x = 0; x < field.length; x++){
            message.append(numberToEmoji(x+1)).append(" ");

            for(int y = 0; y < field[x].length; y++){
                if(field[x][y].equals(flag))
                    message.append(field[x][y]).append(" ");
                else
                    message.append("||").append(field[x][y]).append("|| ");
            }
            message.append("\n");
        }

        String msw = message.substring(0, message.length()-2);
        StringBuilder topRow = new StringBuilder();
        topRow.append(blankTile.getAsMention()).append(" ");
        for(int i = 0; i < field[0].length; i++){
            topRow.append(letterToEmoji(StringUtil.numberToAlphabet(i))).append(" ");
        }
        msw = topRow.toString() + "\n" + msw;

        return msw;
    }

    public String[][] generateMinesweeper(int mines){
        String[][] field = new String[ROWS][COLUMNS];

        // Fill with zeroes
        for(int x = 0; x < ROWS; x++){
            Arrays.fill(field[x], ":zero:");
        }

        // Randomly add mines
        for(int i = 0; i < mines; i++){
            int x, y;
            do{
                x = (int)(Math.random()*field.length);
                y = (int)(Math.random()*field[x].length);
            }
            while(field[x][y].equals(bomb));

            field[x][y] = bomb;
        }

        // Fix number values
        for(int x = 0; x < field.length;x++){
            for(int y = 0; y < field[x].length; y++){
                if(field[x][y].equals(bomb)) continue;
                int totalBombs = 0;
                boolean hasLeft = y > 0;
                boolean hasRight = y < (COLUMNS-1);
                boolean hasTop = x > 0;
                boolean hasBottom = x < (ROWS-1);

                //left
                totalBombs += (hasLeft && field[x][y-1].equals(bomb) ? 1 : 0);

                //top left
                totalBombs += (hasLeft && hasTop && field[x-1][y-1].equals(bomb) ? 1 : 0);

                //top
                totalBombs += (hasTop && field[x-1][y].equals(bomb) ? 1 : 0);

                //top right
                totalBombs += (hasTop && hasRight && field[x-1][y+1].equals(bomb) ? 1 : 0);

                //right
                totalBombs += (hasRight && field[x][y+1].equals(bomb) ? 1 : 0);

                //bottom right
                totalBombs += (hasRight && hasBottom && field[x+1][y+1].equals(bomb) ? 1 : 0);

                //bottom
                totalBombs += (hasBottom && field[x+1][y].equals(bomb) ? 1 : 0);

                //bottom left
                totalBombs += (hasBottom && hasLeft && field[x+1][y-1].equals(bomb) ? 1 : 0);

                String replacement = ":" + StringUtil.numberToString( totalBombs) + ":";

                field[x][y] = replacement;
            }
        }

        return field;
    }

    public static String[][] copy(String[][] array){
        String[][] copy = new String[array.length][array[0].length];

        for(int i = 0; i < array.length; i++){
            System.arraycopy(array[i], 0, copy[i], 0, array[i].length);
        }

        return copy;
    }

    private String numberToEmoji(int x){
        switch(x){
            case 1:
                return oneEmote.getAsMention();
            case 2:
                return twoEmote.getAsMention();
            case 3:
                return threeEmote.getAsMention();
            case 4:
                return fourEmote.getAsMention();
            case 5:
                return fiveEmote.getAsMention();
            case 6:
                return sixEmote.getAsMention();
            case 7:
                return sevenEmote.getAsMention();
            case 8:
                return eightEmote.getAsMention();
            case 9:
                return nineEmote.getAsMention();
            default:
                return null;
        }
    }

    private String letterToEmoji(char c){
        switch(c){
            case 'a':
                return aEmote.getAsMention();
            case 'b':
                return bEmote.getAsMention();
            case 'c':
                return cEmote.getAsMention();
            case 'd':
                return dEmote.getAsMention();
            case 'e':
                return eEmote.getAsMention();
            case 'f':
                return fEmote.getAsMention();
            case 'g':
                return gEmote.getAsMention();
            case 'h':
                return hEmote.getAsMention();
            case 'i':
                return iEmote.getAsMention();
            default:
                return null;
        }
    }

    public static class MinesweeperResponseListener extends ResponseListener {

        private boolean failedAttempt = false;

        public MinesweeperResponseListener(ChannelType channelType, long channelId, long userId, long timeout, Consumer<ResponseListener> handler, Consumer<ResponseListener> timeoutHandler) {
            super(channelType, channelId, userId, timeout, handler, timeoutHandler);
        }

        public boolean isFailedAttempt() {
            return failedAttempt;
        }

        public void setFailedAttempt(boolean failedAttempt) {
            this.failedAttempt = failedAttempt;
        }
    }
}
