package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Splat1Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;

public class SplatProfile {
    private final long userid;
    public String wiiu_nnid, wiiu_pnid;
    public long switch_fc = -1;

    public long pbID = 0;
    public Splat2Profile splat2Profile = new Splat2Profile();
    public Splat1Profile splat1Profile = new Splat1Profile();

    public SplatProfile(long userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "SplatProfile{" +
                "userid=" + userid +
                ", wiiu_nnid='" + wiiu_nnid + '\'' +
                ", wiiu_pnid='" + wiiu_pnid + '\'' +
                ", switch_fc=" + switch_fc +
                ", splat1Profile=" + splat1Profile.toJson() +
                ", splat2Profile=" + splat2Profile.toJson() +
                '}';
    }

    public long getUserID() {
        return userid;
    }
}
