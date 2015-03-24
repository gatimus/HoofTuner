package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Map;

import io.github.gatimus.hooftuner.pvl.Station;
import twitter4j.AccountSettings;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.Friendship;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.PagableResponseList;
import twitter4j.Place;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Trends;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.api.HelpResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TweetFragment extends ListFragment implements ListView.OnItemClickListener {

    private static final String TWITTER_URL = "twitter_url";
    private String twitterURL;
    private ListView listView;
    private ProgressBar progressBar;

    private AsyncTwitter twitter;
    private long[] userIDs;
    private TwitterStream stream;

    private ArrayList<Status> tweets;
    private TweetAdapter tweetAdapter;

    public static TweetFragment newInstance(Station station) {
        Log.v("TweetFragment", "newInstance");
        TweetFragment fragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putString(TWITTER_URL, station.twitter_url.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public TweetFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.v(getClass().getSimpleName(), "attach");
        super.onAttach(activity);
        //TODO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "create");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            twitterURL = getArguments().getString(TWITTER_URL);
        }

        tweets = new ArrayList<Status>();
        tweetAdapter = new TweetAdapter(getActivity(), tweets);
        tweetAdapter.setNotifyOnChange(true);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        if(BuildConfig.DEBUG)cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("7mW6uHRl8gddAZCbKIYXYhTsI")
                .setOAuthConsumerSecret("GIFuoOYD2M3LhTKwkdy1u4ushotItQab960dd8ZlFfCdJdSlVz")
                .setOAuthAccessToken("602173599-jiWfmGF69RWHz0aJr1lTXxHnK9ONBQwGFjH76JKr")
                .setOAuthAccessTokenSecret("ocPLhUJ2HupJU47sKqUOs99eiTM2SPGjpBYgPjwhNGdGK");
        ConfigurationBuilder cb2 = new ConfigurationBuilder();
        if(BuildConfig.DEBUG)cb2.setDebugEnabled(true);
        cb2.setOAuthConsumerKey("7mW6uHRl8gddAZCbKIYXYhTsI")
                .setOAuthConsumerSecret("GIFuoOYD2M3LhTKwkdy1u4ushotItQab960dd8ZlFfCdJdSlVz")
                .setOAuthAccessToken("602173599-jiWfmGF69RWHz0aJr1lTXxHnK9ONBQwGFjH76JKr")
                .setOAuthAccessTokenSecret("ocPLhUJ2HupJU47sKqUOs99eiTM2SPGjpBYgPjwhNGdGK");
        stream = new TwitterStreamFactory(cb.build()).getInstance();
        stream.addListener(new UserStreamListener() {
            @Override
            public void onDeletionNotice(long directMessageId, long userId) {

            }

            @Override
            public void onFriendList(long[] friendIds) {

            }

            @Override
            public void onFavorite(User source, User target, Status favoritedStatus) {

            }

            @Override
            public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

            }

            @Override
            public void onFollow(User source, User followedUser) {

            }

            @Override
            public void onUnfollow(User source, User unfollowedUser) {

            }

            @Override
            public void onDirectMessage(DirectMessage directMessage) {

            }

            @Override
            public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

            }

            @Override
            public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

            }

            @Override
            public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

            }

            @Override
            public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

            }

            @Override
            public void onUserListCreation(User listOwner, UserList list) {

            }

            @Override
            public void onUserListUpdate(User listOwner, UserList list) {

            }

            @Override
            public void onUserListDeletion(User listOwner, UserList list) {

            }

            @Override
            public void onUserProfileUpdate(User updatedUser) {

            }

            @Override
            public void onBlock(User source, User blockedUser) {

            }

            @Override
            public void onUnblock(User source, User unblockedUser) {

            }

            @Override
            public void onStatus(final Status status) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tweets.add(0,status);
                        tweets.remove(tweets.size()-1);
                        tweetAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception ex) {

            }
        });
        twitter = new AsyncTwitterFactory(cb2.build()).getInstance();
        twitter.addListener(new TwitterListener() {
            @Override
            public void gotMentions(ResponseList<Status> statuses) {

            }

            @Override
            public void gotHomeTimeline(ResponseList<Status> statuses) {

            }

            @Override
            public void gotUserTimeline(final ResponseList<Status> statuses) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tweets.addAll(statuses);
                        tweetAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void gotRetweetsOfMe(ResponseList<Status> statuses) {

            }

            @Override
            public void gotRetweets(ResponseList<Status> retweets) {

            }

            @Override
            public void gotShowStatus(Status status) {

            }

            @Override
            public void destroyedStatus(Status destroyedStatus) {

            }

            @Override
            public void updatedStatus(Status status) {

            }

            @Override
            public void retweetedStatus(Status retweetedStatus) {

            }

            @Override
            public void gotOEmbed(OEmbed oembed) {

            }

            @Override
            public void lookedup(ResponseList<Status> statuses) {

            }

            @Override
            public void searched(QueryResult queryResult) {

            }

            @Override
            public void gotDirectMessages(ResponseList<DirectMessage> messages) {

            }

            @Override
            public void gotSentDirectMessages(ResponseList<DirectMessage> messages) {

            }

            @Override
            public void gotDirectMessage(DirectMessage message) {

            }

            @Override
            public void destroyedDirectMessage(DirectMessage message) {

            }

            @Override
            public void sentDirectMessage(DirectMessage message) {

            }

            @Override
            public void gotFriendsIDs(IDs ids) {

            }

            @Override
            public void gotFollowersIDs(IDs ids) {

            }

            @Override
            public void lookedUpFriendships(ResponseList<Friendship> friendships) {

            }

            @Override
            public void gotIncomingFriendships(IDs ids) {

            }

            @Override
            public void gotOutgoingFriendships(IDs ids) {

            }

            @Override
            public void createdFriendship(User user) {

            }

            @Override
            public void destroyedFriendship(User user) {

            }

            @Override
            public void updatedFriendship(Relationship relationship) {

            }

            @Override
            public void gotShowFriendship(Relationship relationship) {

            }

            @Override
            public void gotFriendsList(PagableResponseList<User> users) {

            }

            @Override
            public void gotFollowersList(PagableResponseList<User> users) {

            }

            @Override
            public void gotAccountSettings(AccountSettings settings) {

            }

            @Override
            public void verifiedCredentials(User user) {

            }

            @Override
            public void updatedAccountSettings(AccountSettings settings) {

            }

            @Override
            public void updatedProfile(User user) {

            }

            @Override
            public void updatedProfileBackgroundImage(User user) {

            }

            @Override
            public void updatedProfileColors(User user) {

            }

            @Override
            public void updatedProfileImage(User user) {

            }

            @Override
            public void gotBlocksList(ResponseList<User> blockingUsers) {

            }

            @Override
            public void gotBlockIDs(IDs blockingUsersIDs) {

            }

            @Override
            public void createdBlock(User user) {

            }

            @Override
            public void destroyedBlock(User user) {

            }

            @Override
            public void lookedupUsers(ResponseList<User> users) {
                userIDs = new long[users.size()];
                int i = 0;
                for(User user : users){
                    userIDs[i] = user.getId();
                    Log.v(getClass().getSimpleName(), user.getScreenName() + String.valueOf(user.getId()));
                }
                stream.filter(new FilterQuery().follow(userIDs));
            }

            @Override
            public void gotUserDetail(User user) {

            }

            @Override
            public void searchedUser(ResponseList<User> userList) {

            }

            @Override
            public void gotContributees(ResponseList<User> users) {

            }

            @Override
            public void gotContributors(ResponseList<User> users) {

            }

            @Override
            public void removedProfileBanner() {

            }

            @Override
            public void updatedProfileBanner() {

            }

            @Override
            public void gotMutesList(ResponseList<User> blockingUsers) {

            }

            @Override
            public void gotMuteIDs(IDs blockingUsersIDs) {

            }

            @Override
            public void createdMute(User user) {

            }

            @Override
            public void destroyedMute(User user) {

            }

            @Override
            public void gotUserSuggestions(ResponseList<User> users) {

            }

            @Override
            public void gotSuggestedUserCategories(ResponseList<Category> category) {

            }

            @Override
            public void gotMemberSuggestions(ResponseList<User> users) {

            }

            @Override
            public void gotFavorites(ResponseList<Status> statuses) {

            }

            @Override
            public void createdFavorite(Status status) {

            }

            @Override
            public void destroyedFavorite(Status status) {

            }

            @Override
            public void gotUserLists(ResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListStatuses(ResponseList<Status> statuses) {

            }

            @Override
            public void destroyedUserListMember(UserList userList) {

            }

            @Override
            public void gotUserListMemberships(PagableResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListSubscribers(PagableResponseList<User> users) {

            }

            @Override
            public void subscribedUserList(UserList userList) {

            }

            @Override
            public void checkedUserListSubscription(User user) {

            }

            @Override
            public void unsubscribedUserList(UserList userList) {

            }

            @Override
            public void createdUserListMembers(UserList userList) {

            }

            @Override
            public void checkedUserListMembership(User users) {

            }

            @Override
            public void createdUserListMember(UserList userList) {

            }

            @Override
            public void destroyedUserList(UserList userList) {

            }

            @Override
            public void updatedUserList(UserList userList) {

            }

            @Override
            public void createdUserList(UserList userList) {

            }

            @Override
            public void gotShowUserList(UserList userList) {

            }

            @Override
            public void gotUserListSubscriptions(PagableResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListMembers(PagableResponseList<User> users) {

            }

            @Override
            public void gotSavedSearches(ResponseList<SavedSearch> savedSearches) {

            }

            @Override
            public void gotSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void createdSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void destroyedSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void gotGeoDetails(Place place) {

            }

            @Override
            public void gotReverseGeoCode(ResponseList<Place> places) {

            }

            @Override
            public void searchedPlaces(ResponseList<Place> places) {

            }

            @Override
            public void gotSimilarPlaces(ResponseList<Place> places) {

            }

            @Override
            public void gotPlaceTrends(Trends trends) {

            }

            @Override
            public void gotAvailableTrends(ResponseList<Location> locations) {

            }

            @Override
            public void gotClosestTrends(ResponseList<Location> locations) {

            }

            @Override
            public void reportedSpam(User reportedSpammer) {

            }

            @Override
            public void gotOAuthRequestToken(RequestToken token) {

            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {

            }

            @Override
            public void gotOAuth2Token(OAuth2Token token) {

            }

            @Override
            public void gotAPIConfiguration(TwitterAPIConfiguration conf) {

            }

            @Override
            public void gotLanguages(ResponseList<HelpResources.Language> languages) {

            }

            @Override
            public void gotPrivacyPolicy(String privacyPolicy) {

            }

            @Override
            public void gotTermsOfService(String tof) {

            }

            @Override
            public void gotRateLimitStatus(Map<String, RateLimitStatus> rateLimitStatus) {

            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {

            }
        });
        twitter.getUserTimeline(twitterURL.substring(twitterURL.lastIndexOf("/") + 1));
        twitter.lookupUsers(new String[]{twitterURL.substring(twitterURL.lastIndexOf("/") + 1)});

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "create view");
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
        listView.setEmptyView(progressBar);
        listView.setOnItemClickListener(this);

        listView.setAdapter(tweetAdapter);


        // Set the adapter
        setListAdapter(tweetAdapter);


        return view;
    }

    @Override
    public void onDetach() {
        Log.v(getClass().getSimpleName(), "detach");
        super.onDetach();
    } //onDetach


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(getClass().getSimpleName(), "item click");
        //TODO
    } //onItemClick

} //class
