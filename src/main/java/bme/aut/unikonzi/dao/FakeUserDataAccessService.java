//package bme.aut.unikonzi.dao;
//
//import bme.aut.unikonzi.model.User;
//import org.bson.types.ObjectId;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Repository("fakeDao")
//public class FakeUserDataAccessService implements UserDao {
//
//    private static List<User> DB = new ArrayList<>();
//
//    @Override
//    public int insertUser(ObjectId id, User user) {
//        DB.add(new User(id, user.getName()));
//        return 1;
//    }
//
//    @Override
//    public List<User> selectAllUsers() {
//        return DB;
//    }
//
//    @Override
//    public Optional<User> selectUserById(ObjectId id) {
//        return DB.stream()
//                .filter(user -> user.getId().equals(id))
//                .findFirst();
//    }
//
//    @Override
//    public int deleteUserById(ObjectId id) {
//        Optional<User> userMaybe = selectUserById(id);
//        if (userMaybe.isEmpty()) {
//            return 0;
//        }
//        DB.remove(userMaybe.get());
//        return 1;
//    }
//
//    @Override
//    public int updateUserById(ObjectId id, User user) {
//        return selectUserById(id)
//                .map(u -> {
//                   int indexOfUserToUpdate = DB.indexOf(u);
//                   if (indexOfUserToUpdate >= 0) {
//                       //DB.set(indexOfUserToUpdate, new User(id, user.getName()));
//                       return 1;
//                   }
//                   return 0;
//                })
//                .orElse(0);
//    }
//}
