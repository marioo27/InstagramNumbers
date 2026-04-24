package mgr.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that manages the loading and analysis of JSON files
 * exported from Instagram to obtain information about followers
 * and following.
 *
 * It reads the following.json and followers_1.json files and calculates:
 * - Users who follow me and I do not follow.
 * - Users with mutual following.
 * - Users I follow and who do not follow me back.
 *
 * @author Mario Garcia
 * @version 1.0
 * @since 1.0
 */
public class JsonManager {

    private final ObjectMapper mapper;
    private final String followingPath;
    private final String followersPath;

    private Set<String> following;
    private Set<String> followers;

    /**
     * Creates a new JsonManager instance with the paths to the JSON files.
     *
     * @param followingPath path to the following.json file
     * @param followersPath path to the followers_1.json file
     */
    public JsonManager(String followingPath, String followersPath) {
        this.mapper = new ObjectMapper();
        this.followingPath = followingPath;
        this.followersPath = followersPath;
    }

    /**
     * Loads the data from the JSON files into memory.
     *
     * @throws IOException if an error occurs while reading the files
     */
    public void loadData() throws IOException {
        this.following = loadFollowing();
        this.followers = loadFollowers();
    }

    /**
     * Gets the users who follow me and whom I do not follow back.
     *
     * @return set of usernames who follow me and I do not follow back
     */
    public Set<String> getFollowersNoFollowBack() {
        checkLoaded();
        Set<String> result = new HashSet<>(followers);
        result.removeAll(following);
        return result;
    }

    /**
     * Gets the users with mutual following.
     *
     * @return set of usernames with mutual following
     */
    public Set<String> getMutualFollowers() {
        checkLoaded();
        Set<String> result = new HashSet<>(followers);
        result.retainAll(following);
        return result;
    }

    /**
     * Gets the users I follow who do not follow me back.
     *
     * @return set of usernames I follow and who do not follow me back
     */
    public Set<String> getFollowingNoFollowBack() {
        checkLoaded();
        Set<String> result = new HashSet<>(following);
        result.removeAll(followers);
        return result;
    }

    /**
     * Checks that the data has been loaded correctly.
     *
     * @throws IllegalStateException if the data has not been loaded
     */
    private void checkLoaded() {
        if (following == null || followers == null) {
            throw new IllegalStateException("Data is not loaded. Call loadData() first.");
        }
    }

    /**
     * Loads from following.json the set of users I follow.
     *
     * @return set of followed usernames
     * @throws IOException if an error occurs while reading the file
     */
    private Set<String> loadFollowing() throws IOException {
        JsonNode root = mapper.readTree(new File(followingPath));
        JsonNode relationships = root.get("relationships_following");

        Set<String> result = new HashSet<>();

        if (relationships != null && relationships.isArray()) {
            for (JsonNode node : relationships) {
                JsonNode titleNode = node.get("title");
                if (titleNode != null && !titleNode.isNull()) {
                    String username = titleNode.asText();
                    if (username != null && !username.isBlank()) {
                        result.add(username);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Loads from followers_1.json the set of users who follow me.
     *
     * @return set of follower usernames
     * @throws IOException if an error occurs while reading the file
     */
    private Set<String> loadFollowers() throws IOException {
        JsonNode array = mapper.readTree(new File(followersPath));

        Set<String> result = new HashSet<>();

        if (array != null && array.isArray()) {
            for (JsonNode node : array) {
                JsonNode stringList = node.get("string_list_data");
                if (stringList != null && stringList.isArray() && stringList.size() > 0) {
                    JsonNode first = stringList.get(0);
                    JsonNode valueNode = first.get("value");
                    if (valueNode != null && !valueNode.isNull()) {
                        String username = valueNode.asText();
                        if (username != null && !username.isBlank()) {
                            result.add(username);
                        }
                    }
                }
            }
        }
        return result;
    }
}