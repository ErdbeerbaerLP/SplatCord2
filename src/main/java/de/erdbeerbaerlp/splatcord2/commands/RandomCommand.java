package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Random;

public class RandomCommand extends BaseCommand {

    public RandomCommand(Locale l) {
        super("random", l.botLocale.cmdRandomDesc);
        final SubcommandData weapon = new SubcommandData("weapon", l.botLocale.cmdRandomWeaponDesc);
        weapon.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        final SubcommandData stage = new SubcommandData("stage", l.botLocale.cmdRandomStageDesc);
        stage.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        final SubcommandData team = new SubcommandData("private", l.botLocale.cmdRandomPrivateDesc);
        team.addOption(OptionType.INTEGER, "players", l.botLocale.cmdRandomTeamAmountDesc);
        team.addOption(OptionType.BOOLEAN, "weapons", l.botLocale.cmdRandomTeamWeapons);
        final SubcommandData mode = new SubcommandData("mode", l.botLocale.cmdRandomMode);
        OptionData splVersions = new OptionData(OptionType.INTEGER, "version", l.botLocale.cmdRandomModeVersion);
        splVersions.addChoice("Splatoon 1", 1);
        splVersions.addChoice("Splatoon 2", 2);
        //splVersions.addChoice("Splatoon 3", 3);
        mode.addOptions(splVersions);
        addSubcommands(weapon, stage, team, mode);
    }

    static void shuffleArray(int[] ar) {
        final Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Random r = new Random();
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final String subcmd = ev.getSubcommandName();
        if (subcmd == null) return;
        int amount = 1;
        final OptionMapping amountOption = ev.getOption("amount");
        if (amountOption != null) try {
            amount = Math.min(Integer.parseInt(amountOption.getAsString()), 10);
        } catch (NumberFormatException ignored) {
        }
        final Weapon[] weapons = lang.weapons.values().toArray(new Weapon[0]);
        System.out.println(subcmd);
        switch (subcmd) {
            case "weapon":
                final StringBuilder weaponString = new StringBuilder();
                for (int i = 0; i < amount; ++i) {
                    final int weapon = r.nextInt(lang.weapons.size() - 1);
                    weaponString.append(weapons[weapon].name).append("\n");
                }
                ev.reply(weaponString.toString().trim()).queue();
                break;
            case "stage":
                final StringBuilder stageString = new StringBuilder();
                final Stage[] stages = lang.stages.values().toArray(new Stage[0]);
                for (int i = 0; i < amount; ++i) {
                    final int stage = r.nextInt(lang.stages.size() - 1);
                    stageString.append(stages[stage].getName()).append("\n");
                }
                ev.reply(stageString.toString().trim()).queue();
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
                final int[] playerArray = new int[players];
                for (int i = 0; i < players; i++) {
                    playerArray[i] = i+1;
                }
                shuffleArray(playerArray);
                final StringBuilder privateString = new StringBuilder();
                privateString.append(lang.botLocale.cmdRandomPrivateMode +": "+lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name).append("\n\n");
                privateString.append(lang.botLocale.cmdRandomPrivateAlpha +":\n");
                privateString.append("[" + playerArray[0]+"] ");
                if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                privateString.append("\n");
                if (players >= 4) {
                    privateString.append("[" + playerArray[2]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                if (players >= 6) {
                    privateString.append("[" + playerArray[4]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                if (players >= 8) {
                    privateString.append("[" + playerArray[6]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                privateString.append(lang.botLocale.cmdRandomPrivateBravo +":\n");
                privateString.append("[" + playerArray[1]+"] ");
                if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                privateString.append("\n");
                if (players >= 4) {
                    privateString.append("[" + playerArray[3]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                if (players >= 6) {
                    privateString.append("[" + playerArray[5]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                if (players >= 8) {
                    privateString.append("[" + playerArray[7]+"] ");
                    if(genWeapons) privateString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    privateString.append("\n");
                }
                privateString.append(lang.botLocale.cmdRandomPrivateSpec +":\n");
                if(players == 3) privateString.append("[" + playerArray[2] + "]\n");
                if(players == 5) privateString.append("[" + playerArray[4] + "]\n");
                if(players == 7) privateString.append("[" + playerArray[6] + "]\n");
                if (players >= 9) privateString.append("[" + playerArray[8] + "]\n");
                if (players == 10) privateString.append("[" + playerArray[9] + "]\n");
                ev.reply(privateString.toString().trim()).queue();
                break;
            case "mode":
                final StringBuilder modeString = new StringBuilder();
                int splVer = 2;
                final OptionMapping versionOption = ev.getOption("version");
                if (versionOption != null) try {
                    splVer = Integer.parseInt(versionOption.getAsString());
                } catch (NumberFormatException ignored) {
                }

                switch (splVer){
                    case 1:
                        String mode;
                        do {
                            mode = lang.rules.keySet().toArray(new String[0])[new Random().nextInt(lang.rules.size())];
                        }while(mode.equals("clam_blitz"));
                        modeString.append(lang.rules.get(mode).name);
                    break;
                    case 2:
                        modeString.append(lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name);
                        break;
                    default:
                        break;
                }
                ev.reply(modeString.toString().trim()).queue();
                break;
        }
    }
}
