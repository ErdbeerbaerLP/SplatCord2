package de.erdbeerbaerlp.splatcord2.translation;

public class Japanese  extends EnglishBase{
    {
        languageSetMessage = "Your server's language is now set to japanese\nFor now, only game data will be translated";
    }

    public String getS1MapName(int mapid) {
        switch (mapid) {
            case 0:
                return "デカライン高架下";
            case 1:
                return "ハコフグ倉庫";
            case 2:
                return "シオノメ油田";
            case 3:
                return "アロワナモール";
            case 4:
                return "Ｂバスパーク";
            case 5:
                return "モンガラキャンプ場";
            case 6:
                return "ホッケふ頭";
            case 7:
                return "モズク農園";
            case 8:
                return "タチウオパーキング";
            case 9:
                return "ネギトロ炭鉱";
            case 10:
                return "マサバ海峡大橋";
            case 11:
                return "ヒラメが丘団地";
            case 12:
                return "キンメダイ美術館";
            case 13:
                return "アンチョビットゲームズ";
            case 14:
                return "ショッツル鉱山";
            case 15:
                return "マヒマヒリゾート＆スパ";
            default:
                return super.getS1MapName(mapid);
        }
    }

    public String getS3MapName(int mapid) {
        switch (mapid) {
            default: return null;
        }
    }
    public String getS3SRTitle(int title) {
        switch (title) {
            case 0:
                return "Apprentice";
            case 1:
                return "Part-Timer";
            case 2:
                return "Go-Getter";
            case 3:
                return "Overachiever";
            case 4:
                return "Profreshional";
            case 5:
                return "Profreshional +1";
            case 6:
                return "Profreshional +2";
            case 7:
                return "Profreshional +3";
            case 8:
            default:
                return "Eggsecutive VP";
        }
    }


    public String getSplatfestTeam(int id) {
        switch (id) {
            case 1:
                return "石";
            case 2:
                return "紙";
            case 3:
                return "はさみ";
            default: return super.getSplatfestTeam(id);
        }
    }
}
