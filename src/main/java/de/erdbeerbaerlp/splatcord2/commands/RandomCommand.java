package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.GameRule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Stage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.TranslationNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCommand extends BaseCommand {
    final String[] weapons = Main.weaponData.keySet().toArray(new String[0]);
    public ArrayList<String> lastRandomWeapons = new ArrayList<>();
    public ArrayList<String> lastRandomWeapons3 = new ArrayList<>();

    public RandomCommand(Locale l) {
        super("random", l.botLocale.cmdRandomDesc);
        final SubcommandData weapon = new SubcommandData("weapon", l.botLocale.cmdRandomWeaponDesc);
        final OptionData amount = new OptionData(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        weapon.addOptions(amount);
        final SubcommandData number = new SubcommandData("number", l.botLocale.cmdRandomWeaponDesc);
        final OptionData min = new OptionData(OptionType.INTEGER, "maximum", l.botLocale.cmdRandomNumMin, true);
        final OptionData max = new OptionData(OptionType.INTEGER, "minimum", l.botLocale.cmdRandomNumMax, false);
        number.addOptions(min,max);
        final SubcommandData stage = new SubcommandData("stage", l.botLocale.cmdRandomStageDesc);
        stage.addOptions(amount);
        final SubcommandData team = new SubcommandData("private", l.botLocale.cmdRandomPrivateDesc);
        final OptionData players = new OptionData(OptionType.INTEGER, "players", l.botLocale.cmdRandomTeamAmountDesc);
        final OptionData wpns = new OptionData(OptionType.BOOLEAN, "weapons", l.botLocale.cmdRandomTeamWeapons);
        team.addOptions(players,wpns);
        final SubcommandData mode = new SubcommandData("mode", l.botLocale.cmdRandomMode);
        OptionData splVersions = new OptionData(OptionType.INTEGER, "version", l.botLocale.cmdRandomModeVersion);
        OptionData splVersions2 = new OptionData(OptionType.INTEGER, "version", l.botLocale.cmdRandomModeVersion);
        splVersions.addChoice("Splatoon 3", 3);
        splVersions2.addChoice("Splatoon 3", 3);
        splVersions.addChoice("Splatoon 2", 2);
        splVersions2.addChoice("Splatoon 2", 2);
        splVersions2.addChoice("Splatoon 1", 1);
        weapon.addOptions(splVersions);
        stage.addOptions(splVersions);
        team.addOptions(splVersions);
        mode.addOptions(splVersions2);
        addSubcommands(weapon, number, stage, team, mode);


        splVersions.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomModeVersion"));
        splVersions2.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomModeVersion"));
        number.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
        amount.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomAmountDesc"));
        min.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomNumMin"));
        max.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomNumMax"));
        players.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomTeamAmountDesc"));
        wpns.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomTeamWeapons"));
        mode.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRandomMode"));
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
    }

    @Override
    public boolean isServerOnly() {
        return false;
    }

    static <T> void shuffleArray(T[] ar) {
        final Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    private void addWeapon(String wpnID) {
        lastRandomWeapons.add(wpnID);
        if (lastRandomWeapons.size() > 8) lastRandomWeapons.remove(0);
    }

    private void addWeapon3(String wpnID) {
        lastRandomWeapons3.add(wpnID);
        if (lastRandomWeapons3.size() > 8) lastRandomWeapons3.remove(0);
    }

    private String getRandomWeaponID(Locale lang) {
        final Random r = new Random();
        do {
            final int weapon = r.nextInt(weapons.length - 1);
            final String wpnid = weapons[weapon];
            if (!lang.weapons.containsKey(Integer.parseInt(wpnid)))
                continue;
            if (lastRandomWeapons.contains(wpnid))
                continue;
            addWeapon(wpnid);
            return wpnid;
        } while (true);
    }

    private Weapon getRandomWeapon3() {
        final HashMap<String, Weapon> allWeapons = LInk3.getAllWeapons();
        final Set<String> ids = allWeapons.keySet();
        final Random r = new Random();
        do {
            final int weapon = r.nextInt(ids.size() - 1);
            final String wpnid = ids.toArray(new String[0])[weapon];
            if (!allWeapons.containsKey(wpnid))
                continue;
            if (lastRandomWeapons3.contains(wpnid))
                continue;
            addWeapon3(wpnid);
            return allWeapons.get(wpnid);
        } while (true);
    }

    private String getRandomWeaponName(Locale lang) {
        return lang.weapons.get(Integer.parseInt(getRandomWeaponID(lang))).name;
    }

    private String getRandomS3WeaponName(Locale lang) {
        return getRandomWeapon3().localizedName.get(lang.botLocale.locale.replace("-", "_"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Random r = new Random();
        BotLanguage serverLang = Main.iface.getServerLang(ev.getGuild().getIdLong());
        if(serverLang == null){
            serverLang = BotLanguage.fromDiscordLocale(ev.getGuild().getLocale());
        }
        final Locale lang = Main.translations.get(serverLang);
        final String subcmd = ev.getSubcommandName();
        if (subcmd == null) return;
        int amount = 1;
        final OptionMapping amountOption = ev.getOption("amount");
        if (amountOption != null) try {
            amount = Math.min(Integer.parseInt(amountOption.getAsString()), 10);
        } catch (NumberFormatException ignored) {
        }
        int splVer = 3;
        final OptionMapping versionOption = ev.getOption("version");
        if (versionOption != null) try {
            splVer = Integer.parseInt(versionOption.getAsString());
        } catch (NumberFormatException ignored) {
        }
        switch (subcmd) {
            case "weapon":
                final MessageCreateBuilder mb = new MessageCreateBuilder();
                switch (splVer) {
                    case 2:
                        final ArrayList<MessageEmbed> embeds = new ArrayList<>();
                        for (int i = 0; i < amount; ++i) {
                            final String wpnid = getRandomWeaponID(lang);
                            final de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.weapons.Weapon wpn = Main.weaponData.get(wpnid);
                            embeds.add(new EmbedBuilder()
                                    .setTitle(lang.weapons.get(Integer.parseInt(wpnid)).name)
                                    .setThumbnail("https://splatoon2.ink/assets/splatnet" + wpn.image)
                                    .addField(lang.botLocale.weaponSub, lang.weapon_subs.get(wpn.sub.id).name, true)
                                    .addField(lang.botLocale.weaponSpecial, lang.weapon_specials.get(wpn.special.id).name, true)
                                    .build());
                        }
                        mb.setEmbeds(embeds);
                        break;
                    case 3:
                        final ArrayList<MessageEmbed> embs = new ArrayList<>();
                        for (int i = 0; i < amount; ++i) {
                            final Weapon w = getRandomWeapon3();
                            embs.add(new EmbedBuilder()
                                    .setTitle(w.localizedName.get(lang.botLocale.locale.replace("-", "_")))
                                    .setThumbnail("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/" + w.image)
                                    .addField(lang.botLocale.weaponSub,
                                            LInk3.getSimpleTranslatableByName(w.sub).localizedName.get(lang.botLocale.locale.replace("-", "_")), true)
                                    .addField(lang.botLocale.weaponSpecial, LInk3.getSimpleTranslatableByName(w.special).localizedName.get(lang.botLocale.locale.replace("-", "_")), true)
                                    .build());
                        }
                        mb.setEmbeds(embs);
                        break;
                }

                ev.reply(mb.setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).build()).queue();
                break;
            case "number":
                long minimumNumber = 0;
                long maximumNumber = Long.parseLong(ev.getOption("maximum").getAsString());
                final OptionMapping minOption = ev.getOption("minimum");
                if (minOption != null) minimumNumber = Long.parseLong(minOption.getAsString());
                if (minimumNumber > maximumNumber)
                    ev.reply(lang.botLocale.cmdRandomNumMinMaxError + ThreadLocalRandom.current().nextLong(maximumNumber, minimumNumber + 1)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                else
                    ev.reply(ThreadLocalRandom.current().nextLong(minimumNumber, maximumNumber + 1) + "").setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                break;
            case "stage":
                final StringBuilder stageString = new StringBuilder();
                switch (splVer) {
                    case 2:
                        final Stage[] stages = lang.stages.values().toArray(new Stage[0]);
                        for (int i = 0; i < amount; ++i) {
                            final int stage = r.nextInt(lang.stages.size() - 1);
                            stageString.append(stages[stage].getName()).append("\n");
                        }
                        ev.reply(stageString.toString().trim()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                        break;
                    case 3:
                        final TranslationNode[] s3Stages = lang.s3locales.stages.values().toArray(new TranslationNode[0]);
                        for (int i = 0; i < amount; ++i) {
                            final int stage = r.nextInt(lang.s3locales.stages.size() - 1);
                            stageString.append(s3Stages[stage].name).append("\n");
                        }
                        ev.reply(stageString.toString().trim()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                        break;
                }
                break;
            case "private":
                boolean genWeapons = false;
                final OptionMapping weaponOption = ev.getOption("weapons");
                if (weaponOption != null) genWeapons = weaponOption.getAsBoolean();

                int players = 10;
                final OptionMapping playersOption = ev.getOption("players");
                if (playersOption != null) try {
                    players = Math.max(3, Math.min(Integer.parseInt(playersOption.getAsString()), 10));
                } catch (NumberFormatException ignored) {
                }
                final Integer[] playerArray = new Integer[players];
                for (int i = 0; i < players; i++) {
                    playerArray[i] = i + 1;
                }
                if (splVer == 2) {
                    shuffleArray(playerArray);
                    final StringBuilder privateString = new StringBuilder();
                    privateString.append(lang.botLocale.mode + ": " + lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name).append("\n");
                    final Stage[] stages = lang.stages.values().toArray(new Stage[0]);
                    final int stage = r.nextInt(lang.stages.size() - 1);
                    privateString.append(lang.botLocale.salmonStage+stages[stage].getName()+"\n\n");
                    privateString.append(lang.botLocale.cmdRandomPrivateAlpha + ":\n");
                    privateString.append("[" + playerArray[0] + "] ");
                    if (genWeapons)
                        privateString.append(getRandomWeaponName(lang));
                    privateString.append("\n");
                    if (players >= 4) {
                        privateString.append("[" + playerArray[2] + "] ");
                        if (genWeapons) {
                            privateString.append(getRandomWeaponName(lang));
                        }
                        privateString.append("\n");
                    }
                    if (players >= 6) {
                        privateString.append("[" + playerArray[4] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomWeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 8) {
                        privateString.append("[" + playerArray[6] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomWeaponName(lang));
                        privateString.append("\n");
                    }
                    privateString.append(lang.botLocale.cmdRandomPrivateBravo + ":\n");
                    privateString.append("[" + playerArray[1] + "] ");
                    if (genWeapons)
                        privateString.append(getRandomWeaponName(lang));
                    privateString.append("\n");
                    if (players >= 4) {
                        privateString.append("[" + playerArray[3] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomWeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 6) {
                        privateString.append("[" + playerArray[5] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomWeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 8) {
                        privateString.append("[" + playerArray[7] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomWeaponName(lang));
                        privateString.append("\n");
                    }
                    privateString.append(lang.botLocale.cmdRandomPrivateSpec + ":\n");
                    if (players == 3) privateString.append("[" + playerArray[2] + "]\n");
                    if (players == 5) privateString.append("[" + playerArray[4] + "]\n");
                    if (players == 7) privateString.append("[" + playerArray[6] + "]\n");
                    if (players >= 9) privateString.append("[" + playerArray[8] + "]\n");
                    if (players == 10) privateString.append("[" + playerArray[9] + "]\n");
                    ev.reply(privateString.toString().trim()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                } else if (splVer == 3) {
                    shuffleArray(playerArray);
                    final StringBuilder privateString = new StringBuilder();
                    privateString.append(lang.botLocale.mode + ": " + lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name).append("\n");
                    final TranslationNode[] s3Stages = lang.s3locales.stages.values().toArray(new TranslationNode[0]);
                    final int stage = r.nextInt(lang.s3locales.stages.size() - 1);
                    privateString.append(lang.botLocale.salmonStage + s3Stages[stage].name + "\n\n");
                    privateString.append(lang.botLocale.cmdRandomPrivateAlpha + ":\n");
                    privateString.append("[" + playerArray[0] + "] ");
                    if (genWeapons)
                        privateString.append(getRandomS3WeaponName(lang));
                    privateString.append("\n");
                    if (players >= 4) {
                        privateString.append("[" + playerArray[2] + "] ");
                        if (genWeapons) {
                            privateString.append(getRandomS3WeaponName(lang));
                        }
                        privateString.append("\n");
                    }
                    if (players >= 6) {
                        privateString.append("[" + playerArray[4] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomS3WeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 8) {
                        privateString.append("[" + playerArray[6] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomS3WeaponName(lang));
                        privateString.append("\n");
                    }
                    privateString.append(lang.botLocale.cmdRandomPrivateBravo + ":\n");
                    privateString.append("[" + playerArray[1] + "] ");
                    if (genWeapons)
                        privateString.append(getRandomS3WeaponName(lang));
                    privateString.append("\n");
                    if (players >= 4) {
                        privateString.append("[" + playerArray[3] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomS3WeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 6) {
                        privateString.append("[" + playerArray[5] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomS3WeaponName(lang));
                        privateString.append("\n");
                    }
                    if (players >= 8) {
                        privateString.append("[" + playerArray[7] + "] ");
                        if (genWeapons)
                            privateString.append(getRandomS3WeaponName(lang));
                        privateString.append("\n");
                    }
                    privateString.append(lang.botLocale.cmdRandomPrivateSpec + ":\n");
                    if (players == 3) privateString.append("[" + playerArray[2] + "]\n");
                    if (players == 5) privateString.append("[" + playerArray[4] + "]\n");
                    if (players == 7) privateString.append("[" + playerArray[6] + "]\n");
                    if (players >= 9) privateString.append("[" + playerArray[8] + "]\n");
                    if (players == 10) privateString.append("[" + playerArray[9] + "]\n");
                    ev.reply(privateString.toString().trim()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();

                }
                break;
            case "mode":
                final StringBuilder modeString = new StringBuilder();
                switch (splVer) {
                    case 1:
                        String mode;
                        do {
                            mode = lang.rules.keySet().toArray(new String[0])[new Random().nextInt(lang.rules.size())];
                        } while (mode.equals("clam_blitz"));
                        modeString.append(lang.rules.get(mode).name);
                        break;
                    case 2:
                        modeString.append(lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name);
                        break;
                    case 3:
                        modeString.append(lang.s3locales.rules.values().toArray(new TranslationNode[0])[new Random().nextInt(lang.rules.size())].name);
                        break;
                    default:
                        break;
                }
                ev.reply(modeString.toString().trim()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                break;
        }
    }
}
