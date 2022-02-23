package dashboard;
import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.users.User;
import dto.dtoServer.users.UsersList;
import engine.ClientEngine;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.TableUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class UserListRefresher extends TimerTask {

    private final Consumer<List<TableUser>> usersListConsumer;
    private final Consumer<Integer> totalCreditsConsumer;
    private final ClientEngine clientEngine;
    private final BooleanProperty shouldUpdate;


    public UserListRefresher(BooleanProperty shouldUpdate, Consumer<List<TableUser>> usersListConsumer, Consumer<Integer> totalCreditsConsumer, ClientEngine clientEngine) {
        this.shouldUpdate = shouldUpdate;
        this.usersListConsumer = usersListConsumer;
        this.totalCreditsConsumer = totalCreditsConsumer;
        this.clientEngine = clientEngine;
    }

    private List<TableUser> usersListToListOfUsers(UsersList usersNames){
        User[] users = usersNames.getUsers();
        List<TableUser> tableUsers = new ArrayList<>();
        for(int i = 0; i< usersNames.getLogSize(); ++i){
            TableUser curr = new TableUser(users[i].getName(), users[i].getType());
            tableUsers.add(curr);
        }
        return tableUsers;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        totalCreditsConsumer.accept(clientEngine.getMyCredit());
        HttpClientUtil.runAsync(Constants.USERS_LIST, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                UsersList usersNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfUsersNames,UsersList.class);
                usersListConsumer.accept(usersListToListOfUsers(usersNames));
            }
        });
    }
}