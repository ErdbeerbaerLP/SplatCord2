package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;

public class PrivateCommand extends BaseCommand {

    public PrivateCommand(Locale l) {
        super("private", "This command is still unfinished");//l.botLocale.cmdPrivateDesc

        final SubcommandData join = new SubcommandData("join", "Joins a private room");
        join.addOption(OptionType.STRING, "id", "Room ID", true);

        final SubcommandData leave = new SubcommandData("leave", "Leaves your current private room");

        final SubcommandData create = new SubcommandData("create", "Creates an private battle room");

        final SubcommandData delete = new SubcommandData("delete", "Deletes the private room you created");

        final SubcommandData generate = new SubcommandData("generate", "Generates an match for your current room");

        addSubcommands(join, leave, create, delete, generate);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "join":
                    try {
                        final long roomToJoin = Long.parseLong(ev.getOption("id").getAsString());
                        if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                            ev.reply("You cannot leave your own room. To delete it, use /private delete").setEphemeral(true).queue();
                        } else {
                            if(!Main.iface.roomExists(roomToJoin)){
                                ev.reply("This room does not exist").setEphemeral(true).queue();
                            }else {
                                if(Main.iface.getPlayersInRoom(roomToJoin).size() >= 10){
                                    ev.reply("This room is currently full").queue();
                                }else {
                                    Main.iface.setPlayerRoom(roomToJoin, ev.getUser().getIdLong());
                                    ev.reply("Room joined").queue();
                                }

                            }
                        }
                    }catch(NumberFormatException e){
                        ev.reply("ID has an invalid format!").queue();
                    }
                    break;
                case "leave":
                    if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                        ev.reply("You cannot leave your own room. To delete it, use /private delete").setEphemeral(true).queue();
                    } else {
                        Main.iface.setPlayerRoom(0, ev.getUser().getIdLong());
                        ev.reply("Room left").queue();
                    }
                    break;
                case "create":
                    if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                        ev.reply("You cannot leave your own room. To delete it, use /private delete").setEphemeral(true).queue();
                    } else {
                        long newRoom = TimeUtil.getDiscordTimestamp(Instant.now().toEpochMilli());
                        boolean newPBRoom = Main.iface.createNewPBRoom(newRoom, (short) 2, ev.getMember().getUser().getIdLong());
                        if(newPBRoom){
                            ev.reply("Room created successfully").addEmbeds(new EmbedBuilder().setTitle("Room ID").setDescription(newRoom+"").build()).queue();
                        }else{
                            ev.reply("There was an error creating the room :/").setEphemeral(true).queue();
                        }
                    }
                    break;
                case "delete":
                    long ownedRoom = Main.iface.getOwnedRoom(ev.getUser().getIdLong());
                    if (ownedRoom != 0) {
                        ev.reply(Main.iface.deleteRoom(ownedRoom) + "").queue();
                    } else {
                        ev.reply("You don't own any private room").setEphemeral(true).queue();
                    }
                    break;
                case "generate":
                    try {
                        InteractionHook reply = ev.deferReply().complete();
                        ArrayList<Long> playersInRoom = Main.iface.getPlayersInRoom(Main.iface.getUserRoom(ev.getUser().getIdLong()));

                        if(playersInRoom.size() <=1){
                            reply.editOriginal("Not enough players in Room. You need to be at least two players!").queue();
                            break;
                        }
                        Long[] playerArray = playersInRoom.toArray(new Long[0]);
                        RandomCommand.shuffleArray(playerArray);
                        int players,specs = 0,curPlayer=0;
                        if(playerArray.length == 3 ||playerArray.length == 5 || playerArray.length == 7 || playerArray.length >= 9) specs = 1;
                        if (playerArray.length == 10) specs = 2;

                        players = playerArray.length-specs;

                        BufferedImage alpha = ImageIO.read(getClass().getResourceAsStream("/assets/images/alpha.png"));
                        BufferedImage bravo = ImageIO.read(getClass().getResourceAsStream("/assets/images/bravo.png"));
                        BufferedImage spectator = ImageIO.read(getClass().getResourceAsStream("/assets/images/spec.png"));
                        BufferedImage img = new BufferedImage(400, 40 + (alpha.getHeight()+20) * 2+playersInRoom.size(), BufferedImage.TYPE_INT_ARGB);
                        Font font = new Font("Sans Serif", Font.PLAIN, 24);//createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("fonts/splatfont2.ttf"));

                        Graphics2D g2d = img.createGraphics();
                        g2d.setFont(font);
                        g2d.dispose();

                        g2d = img.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                                RenderingHints.VALUE_DITHER_ENABLE);
                        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                                RenderingHints.VALUE_STROKE_PURE);
                        g2d.setFont(font);
                        FontMetrics fm = g2d.getFontMetrics();
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

                        g2d.setColor(Color.WHITE);
                        g2d.drawString(lang.botLocale.cmdRandomPrivateAlpha, 40, 10 + fm.getAscent());
                        int offset = 35;
                        for (int i = 0; i < players/2; i++) {
                            final Splat2Profile profile = Main.iface.getSplatoonProfiles(playerArray[curPlayer]).splat2Profile;
                            g2d.drawImage(alpha, 20, offset, null);
                            g2d.drawString((profile.getName() != null && !profile.getName().isBlank())?profile.getName():ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName(), 100, offset + 15 + fm.getAscent());
                            offset += alpha.getHeight();
                            curPlayer++;
                        }
                        g2d.drawString(lang.botLocale.cmdRandomPrivateBravo, 40, 10 + offset + fm.getAscent());
                        offset += 35;
                        for (int i = 0; i < players/2; i++) {
                            final Splat2Profile profile = Main.iface.getSplatoonProfiles(playerArray[curPlayer]).splat2Profile;
                            g2d.drawImage(bravo, 20, offset, null);
                            g2d.drawString((profile.getName() != null && !profile.getName().isBlank())?profile.getName():ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName(), 100, offset + 15 + fm.getAscent());
                            offset += bravo.getHeight();
                            curPlayer++;
                        }
                        g2d.drawString(lang.botLocale.cmdRandomPrivateSpec, 40, 10 + offset + fm.getAscent());
                        offset += 35;

                        for (int i = 0; i < specs; i++) {
                            final Splat2Profile profile = Main.iface.getSplatoonProfiles(playerArray[curPlayer]).splat2Profile;
                            g2d.drawImage(spectator, 20, offset, null);
                            g2d.drawString((profile.getName() != null && !profile.getName().isBlank())?profile.getName():ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName(), 100, offset + 15 + fm.getAscent());
                            offset += spectator.getHeight();
                            curPlayer++;
                        }
                        g2d.dispose();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(img, "png", os);
                        reply.editOriginal( os.toByteArray(), "test.png").queue();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        else System.err.println("Umm... subcommand is null...");
    }
}
