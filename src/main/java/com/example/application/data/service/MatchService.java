package com.example.application.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.application.data.repository.*;
import com.example.application.data.repository.MatchRepository;
import com.example.application.data.repository.UserRepository;
import com.example.application.data.entity.*;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
@Service
public class MatchService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MatchRepository matchRepository;
    private static final Logger logger = Logger.getLogger(MatchService.class.getName());
    public boolean alreadyHaveMatchToady(User user, LocalDate today) {
        List<Match> todayMatches = matchRepository.findByMatchedDate(today);
        for (Match todayMatch : todayMatches) {
            if (todayMatch.getUser1().equals(user)) {
                return true;
            } else if (todayMatch.getUser2().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCommonSport(User user1, User user2) {
        return user1.getSports().stream()
                .anyMatch(sports -> user2.getSports().contains(sports));
    }

    private boolean hasCommonMovie(User user1, User user2) {
        return user1.getMovies().stream()
                .anyMatch(movies -> user2.getMovies().contains(movies));
    }

    private boolean hasCommonFood(User user1, User user2) {
        return user1.getFoods().stream()
                .anyMatch(foods -> user2.getFoods().contains(foods));
    }

    public Match findOrCreateMatchForUser(User currentUser) {
        LocalDate today = LocalDate.now();
        //currentUser.setWantMatch(true);

        // 判斷是否有別人已經想配到我了 (我是否已成為為user2)
        // 先按的人是user1，後按的人是user2
        if (currentUser.getOtherGuyWantMatchMe()) {
            logger.info("Current user has already been requested for match.");
            // 另一個等待的人(先按開始配對的人)取消等待
            

            // 把第二個按開始配對的人(也就是我)的"其他人已配到我"關掉
            currentUser.setOtherGuyWantMatchMe(false); 

            //返回"成功"的 match
            return (matchRepository.findByUser2AndMatchedDate(currentUser, today)).get(0); 
            
        } else {
            //我可以去配別人

            if (!alreadyHaveMatchToady(currentUser, today)) {
                logger.info("Current user does not have a match today.");
                // 代表我今天還沒有match，我將成為user1，配對到符合資格的另一人將成為user2

                String targetPrefix = currentUser.getMbti().startsWith("i") ? "e" : "i";
                List<User> targetUsers = userRepository.findByMbtiStartingWith(targetPrefix);
                logger.info("Potential matches found: " + targetUsers.size());

                for (User targetUser : targetUsers) {
                    logger.info("Checking potential match: " + targetUser.getUsername());
                    if (!targetUser.getId().equals(currentUser.getId()) && //找到的人不是自己
                            hasCommonSport(currentUser, targetUser) && //有至少一個共同運動
                            hasCommonMovie(targetUser, currentUser) && //有至少一個共同電影
                            hasCommonFood(targetUser, currentUser)  && //有至少一個共同食物
                            //currentUser.getGender() != targetUser.getGender() // 確保是異性
                            currentUser.getAge().equals(targetUser.getAge()) //年紀相符
                        ) {

                        // 確認以前沒配過這個人
                        boolean checkIfHadMatchedBefore = false;
                        for(Match match : matchRepository.findByUser1(currentUser)){
                            if(match.getOtherUser(currentUser).equals(targetUser)){
                                checkIfHadMatchedBefore = true;
                                break;
                            }
                        }
                        for(Match match : matchRepository.findByUser2(currentUser)){
                            if(match.getOtherUser(currentUser).equals(targetUser)){
                                checkIfHadMatchedBefore = true;
                                break;
                            }
                        }
                        if(checkIfHadMatchedBefore){
                            logger.info("Already matched with user: " + targetUser.getUsername());
                            continue; //下面一位
                        }

                        // 找到符合資格的人，為兩人創建今天的 match (先按的人是user1，後按的人是user2)
                        Match newMatch = new Match(currentUser, targetUser, today); 
                        logger.info("Find the Match");
                        // 讓被配到的人state設為"有人已經配到他了"
                        targetUser.setOtherGuyWantMatchMe(true); 

                        // 返回並儲存此 match到資料庫
                        return matchRepository.save(newMatch); 
                    }
                }

            } else {
                logger.info("No match found for user: " + currentUser.getUsername());
                // 代表我今天已經有 match了，直接返回該 match
                return (matchRepository.findByUser1AndMatchedDate(currentUser, today)).get(0);
            }
        }

        // No match found for the current user
        //沒人配我我也配不到別人
        logger.info("No match found for user: " + currentUser.getUsername());
        return null;
    }

}