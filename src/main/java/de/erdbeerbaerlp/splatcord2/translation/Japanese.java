package de.erdbeerbaerlp.splatcord2.translation;

public class Japanese  extends EnglishBase{
    {
        languageSetMessage = "Your server's language is now set to japanese\nFor now, only game data will be translated";
    }

    public String getS1MapName(int mapid) {
        return switch (mapid) {
            case 0 -> "デカライン高架下";
            case 1 -> "ハコフグ倉庫";
            case 2 -> "シオノメ油田";
            case 3 -> "アロワナモール";
            case 4 -> "Ｂバスパーク";
            case 5 -> "モンガラキャンプ場";
            case 6 -> "ホッケふ頭";
            case 7 -> "モズク農園";
            case 8 -> "タチウオパーキング";
            case 9 -> "ネギトロ炭鉱";
            case 10 -> "マサバ海峡大橋";
            case 11 -> "ヒラメが丘団地";
            case 12 -> "キンメダイ美術館";
            case 13 -> "アンチョビットゲームズ";
            case 14 -> "ショッツル鉱山";
            case 15 -> "マヒマヒリゾート＆スパ";
            default -> super.getS1MapName(mapid);
        };
    }

    public String getS3MapName(int mapid) {
        return switch (mapid) {
            case 1 -> "ユノハナ大渓谷";
            case 2 -> "ゴンズイ地区";
            case 3 -> "ヤガラ市場";
            case 4 -> "マテガイ放水路";
            case 6 -> "ナメロウ金属";
            case 10 -> "マサバ海峡大橋";
            case 11 -> "キンメダイ美術館";
            case 12 -> "マヒマヒリゾート＆スパ";
            case 13 -> "海女美術大学";
            case 14 -> "チョウザメ造船";
            case 15 -> "ザトウマーケット";
            case 16 -> "スメーシーワールド";
            default -> super.getS3MapName(mapid);
        };
    }
    public String getS3SRTitle(int title) {
        return switch (title) {
            case 0 -> "Apprentice";
            case 1 -> "Part-Timer";
            case 2 -> "Go-Getter";
            case 3 -> "Overachiever";
            case 4 -> "Profreshional";
            case 5 -> "Profreshional +1";
            case 6 -> "Profreshional +2";
            case 7 -> "Profreshional +3";
            default -> "Eggsecutive VP";
        };
    }


    public String getSplatfestTeam(int id) {
        return switch (id) {
            case 1 -> "石";
            case 2 -> "紙";
            case 3 -> "はさみ";
            default -> super.getSplatfestTeam(id);
        };
    }
}