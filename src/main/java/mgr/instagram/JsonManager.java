package mgr.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase que gestiona la carga y el analisis de ficheros JSON
 * exportados desde Instagram para obtener informacion de seguidores
 * y seguidos.
 *
 * Lee los ficheros following.json y followers_1.json y calcula:
 * - Usuarios que me siguen y no sigo.
 * - Usuarios con seguimiento mutuo.
 * - Usuarios a los que sigo y no me siguen.
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
     * Crea una nueva instancia de JsonManager con las rutas a los ficheros JSON.
     *
     * @param followingPath ruta al fichero following.json
     * @param followersPath ruta al fichero followers_1.json
     */
    public JsonManager(String followingPath, String followersPath) {
        this.mapper = new ObjectMapper();
        this.followingPath = followingPath;
        this.followersPath = followersPath;
    }

    /**
     * Carga los datos de los ficheros JSON en memoria.
     *
     * @throws IOException si ocurre un error al leer los ficheros
     */
    public void loadData() throws IOException {
        this.following = loadFollowing();
        this.followers = loadFollowers();
    }

    /**
     * Obtiene los usuarios que me siguen y a los que yo no sigo.
     *
     * @return conjunto de nombres de usuario que me siguen y no sigo de vuelta
     */
    public Set<String> getFollowersNoFollowBack() {
        checkLoaded();
        Set<String> result = new HashSet<>(followers);
        result.removeAll(following);
        return result;
    }

    /**
     * Obtiene los usuarios con los que existe seguimiento mutuo.
     *
     * @return conjunto de nombres de usuario con seguimiento mutuo
     */
    public Set<String> getMutualFollowers() {
        checkLoaded();
        Set<String> result = new HashSet<>(followers);
        result.retainAll(following);
        return result;
    }

    /**
     * Obtiene los usuarios a los que sigo y que no me siguen de vuelta.
     *
     * @return conjunto de nombres de usuario que sigo y no me siguen
     */
    public Set<String> getFollowingNoFollowBack() {
        checkLoaded();
        Set<String> result = new HashSet<>(following);
        result.removeAll(followers);
        return result;
    }

    /**
     * Comprueba que los datos han sido cargados correctamente.
     *
     * @throws IllegalStateException si los datos no han sido cargados
     */
    private void checkLoaded() {
        if (following == null || followers == null) {
            throw new IllegalStateException("Los datos no estan cargados. Llama primero a loadData().");
        }
    }

    /**
     * Carga desde following.json el conjunto de usuarios a los que sigo.
     *
     * @return conjunto de nombres de usuario seguidos
     * @throws IOException si ocurre un error al leer el fichero
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
     * Carga desde followers_1.json el conjunto de usuarios que me siguen.
     *
     * @return conjunto de nombres de usuario seguidores
     * @throws IOException si ocurre un error al leer el fichero
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
