import {
  useEffect,
  useState,
} from "react";

import {
  RefreshCw,
  Loader2,
} from "lucide-react";

import socialService from "../../services/socialService";
import PostCard from "./PostCard";

// ============================================================
// GET CURRENT USER FROM JWT
// ============================================================

function getCurrentUserFromToken() {
  try {
    const token =
      localStorage.getItem("token");

    if (!token) {
      return null;
    }

    const parts = token.split(".");

    if (parts.length !== 3) {
      return null;
    }

    const payload = JSON.parse(
      atob(parts[1])
    );

    console.log(
      "CURRENT USER JWT:",
      payload
    );

    return {
      id: payload?.userId || null,
      userId: payload?.userId || null,
      username:
        payload?.sub ||
        payload?.username ||
        "User",
      email:
        payload?.email ||
        null,
    };
  } catch (error) {
    console.error(
      "JWT DECODE ERROR:",
      error
    );

    return null;
  }
}

// ============================================================
// GET CURRENT USER ID
// ============================================================

function getUserIdFromToken() {
  const currentUser =
    getCurrentUserFromToken();

  return currentUser?.id || null;
}

// ============================================================
// FIND USER FOR POST
// ============================================================

function findUserForPost(
  post,
  users,
  currentUser
) {
  if (!post) {
    return null;
  }

  // ==========================================================
  // POST USER ID
  // ==========================================================

  const postUserId =
    post?.userId ||
    post?.user_id ||
    post?.uid;

  if (!postUserId) {
    console.warn(
      "POST DOES NOT HAVE USER ID:",
      post
    );

    return null;
  }

  // ==========================================================
  // CURRENT USER CHECK
  // ==========================================================

  if (
    currentUser?.id &&
    String(postUserId).trim() ===
      String(currentUser.id).trim()
  ) {
    console.log(
      "CURRENT USER MATCHED FROM JWT:",
      {
        postUserId,
        currentUser,
      }
    );

    return currentUser;
  }

  // ==========================================================
  // SEARCH ALL USERS
  // ==========================================================

  if (
    Array.isArray(users) &&
    users.length > 0
  ) {
    const matchingUser =
      users.find((person) => {
        const personId =
          person?.id ||
          person?.userId ||
          person?.user_id ||
          person?.uid;

        if (!personId) {
          return false;
        }

        return (
          String(personId).trim() ===
          String(postUserId).trim()
        );
      });

    if (matchingUser) {
      console.log(
        "USER MATCHED FROM USERS:",
        {
          postUserId,
          matchingUser,
        }
      );

      return matchingUser;
    }
  }

  // ==========================================================
  // NO USER FOUND
  // ==========================================================

  console.warn(
    "NO USER FOUND FOR POST:",
    {
      postUserId,
      usersCount: users?.length || 0,
      currentUser,
    }
  );

  return null;
}

// ============================================================
// FEED COMPONENT
// ============================================================

export default function Feed({
  newPost,
  users = [],
}) {

  // ==========================================================
  // CURRENT USER
  // ==========================================================

  const [
    currentUser,
    setCurrentUser,
  ] = useState(null);

  // ==========================================================
  // POSTS
  // ==========================================================

  const [posts, setPosts] =
    useState([]);

  // ==========================================================
  // LOADING
  // ==========================================================

  const [loading, setLoading] =
    useState(true);

  // ==========================================================
  // ERROR
  // ==========================================================

  const [error, setError] =
    useState("");

  // ==========================================================
  // LOAD CURRENT USER
  // ==========================================================

  useEffect(() => {
    const loggedInUser =
      getCurrentUserFromToken();

    console.log(
      "FEED CURRENT USER:",
      loggedInUser
    );

    setCurrentUser(
      loggedInUser
    );
  }, []);

  // ==========================================================
  // DEBUG USERS
  // ==========================================================

  useEffect(() => {
    console.log(
      "FEED USERS:",
      users
    );

    console.log(
      "FEED USERS COUNT:",
      users.length
    );
  }, [users]);

  // ==========================================================
  // LOAD FEED
  // ==========================================================

  const loadFeed = async () => {
    try {
      setLoading(true);
      setError("");

      // ======================================================
      // CURRENT USER
      // ======================================================

      const userId =
        getUserIdFromToken();

      console.log(
        "CURRENT USER ID:",
        userId
      );

      if (!userId) {
        throw new Error(
          "User ID not found in authentication token."
        );
      }

      // ======================================================
      // FOLLOWING FEED
      // ======================================================

      let feedResponse = null;

      try {
        feedResponse =
          await socialService.getFeed(
            0,
            20
          );

        console.log(
          "FOLLOWING FEED RESPONSE:",
          feedResponse
        );
      } catch (feedError) {
        console.warn(
          "FOLLOWING FEED FAILED:",
          feedError
        );
      }

      // ======================================================
      // OWN POSTS
      // ======================================================

      let ownPostsResponse = null;

      try {
        ownPostsResponse =
          await socialService.getUserPosts(
            userId,
            0,
            50
          );

        console.log(
          "MY POSTS RESPONSE:",
          ownPostsResponse
        );
      } catch (ownPostsError) {
        console.error(
          "MY POSTS ERROR:",
          ownPostsError
        );

        throw ownPostsError;
      }

      // ======================================================
      // FOLLOWING POSTS
      // ======================================================

      const followingPosts =
        Array.isArray(
          feedResponse?.content
        )
          ? feedResponse.content
          : [];

      // ======================================================
      // OWN POSTS
      // ======================================================

      const ownPosts =
        Array.isArray(
          ownPostsResponse?.content
        )
          ? ownPostsResponse.content
          : [];

      console.log(
        "FOLLOWING POSTS:",
        followingPosts
      );

      console.log(
        "MY POSTS:",
        ownPosts
      );

      // ======================================================
      // MERGE
      // ======================================================

      const allPosts = [
        ...followingPosts,
        ...ownPosts,
      ];

      // ======================================================
      // REMOVE DUPLICATES
      // ======================================================

      const uniquePosts =
        Array.from(
          new Map(
            allPosts.map((post) => [
              post.id,
              post,
            ])
          ).values()
        );

      // ======================================================
      // SORT
      // ======================================================

      uniquePosts.sort(
        (a, b) => {
          const dateA =
            new Date(
              a?.createdAt || 0
            ).getTime();

          const dateB =
            new Date(
              b?.createdAt || 0
            ).getTime();

          return dateB - dateA;
        }
      );

      console.log(
        "FINAL SOCIAL POSTS:",
        uniquePosts
      );

      setPosts(
        uniquePosts
      );
    } catch (err) {
      console.error(
        "SOCIAL FEED ERROR:",
        err
      );

      setError(
        err?.message ||
          "Unable to load social feed."
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================================
  // FIRST LOAD
  // ==========================================================

  useEffect(() => {
    loadFeed();
  }, []);

  // ==========================================================
  // NEW POST CREATED
  // ==========================================================

  useEffect(() => {
    if (!newPost) {
      return;
    }

    console.log(
      "NEW POST RECEIVED:",
      newPost
    );

    setPosts(
      (currentPosts) => {
        const alreadyExists =
          currentPosts.some(
            (post) =>
              post.id ===
              newPost.id
          );

        if (alreadyExists) {
          return currentPosts;
        }

        const updatedPosts = [
          newPost,
          ...currentPosts,
        ];

        updatedPosts.sort(
          (a, b) =>
            new Date(
              b?.createdAt || 0
            ).getTime() -
            new Date(
              a?.createdAt || 0
            ).getTime()
        );

        return updatedPosts;
      }
    );
  }, [newPost]);

  // ==========================================================
  // LOADING
  // ==========================================================

  if (
    loading &&
    posts.length === 0
  ) {
    return (
      <div className="space-y-4">
        {[1, 2, 3].map(
          (item) => (
            <div
              key={item}
              className="
                bg-white
                border
                border-[#dfe7e2]
                rounded-3xl
                p-5
                animate-pulse
              "
            >
              <div className="flex gap-3">
                <div
                  className="
                    w-11
                    h-11
                    rounded-2xl
                    bg-[#dfe7e2]
                  "
                />

                <div className="flex-1">
                  <div
                    className="
                      h-3
                      bg-[#dfe7e2]
                      rounded
                      w-1/3
                    "
                  />

                  <div
                    className="
                      h-3
                      bg-[#dfe7e2]
                      rounded
                      w-1/4
                      mt-2
                    "
                  />
                </div>
              </div>

              <div
                className="
                  h-16
                  bg-[#dfe7e2]
                  rounded-2xl
                  mt-5
                "
              />
            </div>
          )
        )}
      </div>
    );
  }

  // ==========================================================
  // ERROR
  // ==========================================================

  if (
    error &&
    posts.length === 0
  ) {
    return (
      <div
        className="
          bg-white
          border
          border-red-200
          rounded-3xl
          p-8
          text-center
        "
      >
        <p
          className="
            text-sm
            font-semibold
            text-red-500
          "
        >
          {error}
        </p>

        <button
          type="button"
          onClick={loadFeed}
          className="
            mt-4
            px-5
            py-2.5
            rounded-2xl
            bg-[#123c2c]
            text-white
            text-xs
            font-bold
          "
        >
          Try Again
        </button>
      </div>
    );
  }

  // ==========================================================
  // EMPTY FEED
  // ==========================================================

  if (posts.length === 0) {
    return (
      <div
        className="
          bg-white
          border
          border-[#dfe7e2]
          rounded-3xl
          p-10
          text-center
        "
      >
        <p className="text-sm font-bold">
          No posts yet
        </p>

        <p
          className="
            text-xs
            text-[#68756f]
            mt-2
          "
        >
          There are no posts in your feed.
        </p>

        <button
          type="button"
          onClick={loadFeed}
          className="
            mt-4
            p-2.5
            rounded-xl
            border
            border-[#dfe7e2]
          "
        >
          <RefreshCw size={16} />
        </button>
      </div>
    );
  }

  // ==========================================================
  // FEED
  // ==========================================================

  return (
    <section className="space-y-5">

      {/* HEADER */}

      <div
        className="
          flex
          items-center
          justify-between
        "
      >
        <div>
          <h2 className="text-lg font-bold">
            Community Feed
          </h2>

          <p
            className="
              text-xs
              text-[#68756f]
            "
          >
            Latest posts from SkillCart
          </p>
        </div>

        <button
          type="button"
          onClick={loadFeed}
          disabled={loading}
          className="
            p-2.5
            rounded-xl
            bg-white
            border
            border-[#dfe7e2]
            hover:bg-[#f7faf8]
            disabled:opacity-50
          "
          title="Refresh feed"
        >
          {loading ? (
            <Loader2
              size={16}
              className="animate-spin"
            />
          ) : (
            <RefreshCw size={16} />
          )}
        </button>
      </div>

      {/* POSTS */}

      {posts.map((post) => {

        const postUser =
          findUserForPost(
            post,
            users,
            currentUser
          );

        console.log(
          "POST CARD USER DATA:",
          {
            postId: post?.id,
            postUserId: post?.userId,
            currentUserId:
              currentUser?.id,
            currentUsername:
              currentUser?.username,
            user: postUser,
          }
        );

        return (
          <PostCard
            key={post.id}
            post={post}
            user={postUser}
            onPostDeleted={
              (deletedPostId) => {
                setPosts(
                  (currentPosts) =>
                    currentPosts.filter(
                      (item) =>
                        item.id !==
                        deletedPostId
                    )
                );
              }
            }
          />
        );
      })}

      {/* BACKGROUND ERROR */}

      {error && (
        <div
          className="
            text-center
            text-xs
            text-red-500
            font-semibold
          "
        >
          {error}
        </div>
      )}
    </section>
  );
}