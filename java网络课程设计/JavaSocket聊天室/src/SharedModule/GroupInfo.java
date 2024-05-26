package SharedModule;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupInfo implements Serializable {
    private static final long serialVersionUID = -4183704331300917684L;
    private String groupId; // Unique identifier for the group
    private String groupName; // Name of the group
    private ArrayList<UserInfo> members; // List of group members
    private UserInfo groupOwner; // The owner of the group

    // Modified constructor to include groupOwner
    public GroupInfo(String groupId, String groupName, UserInfo groupOwner) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = new ArrayList<>();
        this.groupOwner = groupOwner;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<UserInfo> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<UserInfo> members) {
        this.members = members;
    }

    // Getters and Setters for new field
    public UserInfo getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(UserInfo groupOwner) {
        this.groupOwner = groupOwner;
    }

    public void addMember(UserInfo user, UserInfo requestingUser) throws Exception {
        if (!groupOwner.getUserID().equals(requestingUser.getUserID())) {
            throw new Exception("Only the group owner can add new members directly.");
        }
        if (members.contains(user)) {
            throw new Exception("This user is already a member of the group.");
        }
        members.add(user);
    }

    // Existing methods...
    // Adding, removing members, etc.

    // Modified toString() method for debugging purposes to include the group owner
    @Override
    public String toString() {
        StringBuilder memberNames = new StringBuilder();
        for (UserInfo member : members) {
            memberNames.append(member.getNickName()).append(", ");
        }
        // Include group owner in the string representation
        String groupOwnerName = groupOwner != null ? groupOwner.getNickName() : "None";
        return "GroupInfo{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupOwner='" + groupOwnerName + '\'' +
                ", members=[" + (memberNames.length() > 0 ? memberNames.substring(0, memberNames.length() - 2) : "") + "]" +
                '}';
    }
}