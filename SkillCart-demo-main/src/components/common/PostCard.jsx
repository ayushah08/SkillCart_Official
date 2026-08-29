import {
  Heart,
  MessageCircle,
  Send,
  RefreshCw,
  Loader2,
  MoreVertical,
  Trash2,
  X,
} from "lucide-react";

import {
  useEffect,
  useState,
} from "react";

import socialService from "../../services/socialService";
import UserHeader from "./UserHeader";

// ============================================================
// GET CURRENT USER FROM JWT
// ============================================================

function getCurrentUserFromToken() {
  try {
    const token = localStorage.getItem("token");

    if (!token) {
      return null;
    }

    const parts = token.split(".");

    if (parts.length !== 3) {
      return null;
    }

    const payload = JSON.parse(atob(parts[1]));

    console.log("CURRENT USER JWT:", payload);

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

function getCurrentUserId() {
  const currentUser =
    getCurrentUserFromToken();

  return currentUser?.id || null;
}

// ============================================================
// FIND USER BY ID
// ============================================================

function findUserById(
  userId,
  users = []
) {
  if (!userId) {
    return null;
  }

  if (!Array.isArray(users)) {
    return null;
  }

  const foundUser = users.find(
    (person) => {
      const personId =
        person?.id ||
        person?.userId ||
        person?.user_id ||
        person?.uid;

      return (
        personId &&
        String(personId).trim() ===
          String(userId).trim()
      );
    }
  );

  return foundUser || null;
}

// ============================================================
// FIND USER FOR COMMENT
// ============================================================

function findUserForComment(
  comment,
  users,
  currentUser
) {
  if (!comment) {
    return null;
  }

  // ==========================================================
  // CASE 1
  // Backend already sends user object
  // ==========================================================

  if (
    comment?.user &&
    comment.user?.username
  ) {
    return comment.user;
  }

  // ==========================================================
  // GET COMMENT USER ID
  // ==========================================================

  const commentUserId =
    comment?.userId ||
    comment?.user_id ||
    comment?.authorId ||
    comment?.author_id ||
    comment?.uid;

  console.log(
    "COMMENT USER ID:",
    commentUserId
  );

  // ==========================================================
  // CASE 2
  // COMMENT IS FROM CURRENT USER
  // ==========================================================

  if (
    currentUser?.id &&
    commentUserId &&
    String(currentUser.id).trim() ===
      String(commentUserId).trim()
  ) {
    return currentUser;
  }

  // ==========================================================
  // CASE 3
  // FIND USER FROM USERS ARRAY
  // ==========================================================

  const matchingUser =
    findUserById(
      commentUserId,
      users
    );

  if (matchingUser) {
    return matchingUser;
  }

  // ==========================================================
  // NO USER FOUND
  // ==========================================================

  console.warn(
    "NO USER FOUND FOR COMMENT:",
    {
      comment,
      commentUserId,
      usersCount:
        users?.length || 0,
    }
  );

  return null;
}

// ============================================================
// POST CARD
// ============================================================

export default function PostCard({
  post,
  user,
  users = [],
  onPostDeleted,
}) {

  // ============================================================
  // CURRENT USER
  // ============================================================

  const currentUser =
    getCurrentUserFromToken();

  const currentUserId =
    currentUser?.id ||
    getCurrentUserId();

  // ============================================================
  // POST USER
  // ============================================================

  const postUser =
    user || null;

  // ============================================================
  // DEBUG USER INFORMATION
  // ============================================================

  useEffect(() => {
    console.log(
      "POST CARD USER DATA:",
      {
        postId: post?.id,
        postUserId: post?.userId,
        currentUserId,
        currentUsername:
          currentUser?.username,
        user: postUser,
      }
    );
  }, [
    post?.id,
    post?.userId,
    currentUserId,
    currentUser?.username,
    postUser,
  ]);

  // ============================================================
  // CHECK MY POST
  // ============================================================

  const isMyPost =
    Boolean(
      currentUserId &&
      post?.userId &&
      String(currentUserId) ===
        String(post.userId)
    );

  // ============================================================
  // FOLLOW
  // ============================================================

  const [
    isFollowing,
    setIsFollowing,
  ] = useState(false);

  const [
    followLoading,
    setFollowLoading,
  ] = useState(false);

  const [
    followError,
    setFollowError,
  ] = useState("");

  // ============================================================
  // CHECK FOLLOWING STATUS
  // ============================================================

  useEffect(() => {
    if (
      !post?.userId ||
      !currentUserId ||
      isMyPost
    ) {
      return;
    }

    const checkFollowingStatus =
      async () => {
        try {
          const response =
            await socialService.getFollowingStatus(
              post.userId
            );

          console.log(
            "FOLLOWING STATUS:",
            post.userId,
            response
          );

          const following =
            typeof response ===
            "boolean"
              ? response
              : response?.following ??
                response?.isFollowing ??
                response?.followingStatus ??
                response?.data?.following ??
                response?.data?.isFollowing ??
                false;

          setIsFollowing(
            Boolean(following)
          );
        } catch (error) {
          console.error(
            "FOLLOWING STATUS ERROR:",
            error
          );
        }
      };

    checkFollowingStatus();
  }, [
    post?.userId,
    currentUserId,
    isMyPost,
  ]);

  // ============================================================
  // FOLLOW / UNFOLLOW
  // ============================================================

  const handleFollow = async () => {
    if (
      followLoading ||
      !post?.userId ||
      isMyPost
    ) {
      return;
    }

    try {
      setFollowLoading(true);
      setFollowError("");

      if (isFollowing) {
        await socialService.unfollowUser(
          post.userId
        );

        setIsFollowing(false);
      } else {
        await socialService.followUser(
          post.userId
        );

        setIsFollowing(true);
      }
    } catch (error) {
      console.error(
        "FOLLOW / UNFOLLOW ERROR:",
        error
      );

      setFollowError(
        error?.message ||
          "Unable to update follow status."
      );
    } finally {
      setFollowLoading(false);
    }
  };

  // ============================================================
  // LIKE
  // ============================================================

  const [liked, setLiked] =
    useState(
      Boolean(post?.likedByMe)
    );

  const [likeCount, setLikeCount] =
    useState(
      Number(
        post?.likeCount || 0
      )
    );

  const [likeLoading, setLikeLoading] =
    useState(false);

  // ============================================================
  // COMMENTS
  // ============================================================

  const [
    showComments,
    setShowComments,
  ] = useState(false);

  const [comments, setComments] =
    useState([]);

  const [
    commentCount,
    setCommentCount,
  ] = useState(
    Number(
      post?.commentCount || 0
    )
  );

  const [
    commentsLoading,
    setCommentsLoading,
  ] = useState(false);

  const [
    commentSubmitting,
    setCommentSubmitting,
  ] = useState(false);

  const [
    commentText,
    setCommentText,
  ] = useState("");

  const [
    commentError,
    setCommentError,
  ] = useState("");

  // ============================================================
  // DELETE
  // ============================================================

  const [
    showMenu,
    setShowMenu,
  ] = useState(false);

  const [
    showDeleteConfirm,
    setShowDeleteConfirm,
  ] = useState(false);

  const [
    deleteLoading,
    setDeleteLoading,
  ] = useState(false);

  const [
    deleteError,
    setDeleteError,
  ] = useState("");

  // ============================================================
  // LIKE / UNLIKE
  // ============================================================

  const handleLike = async () => {
    if (
      likeLoading ||
      !post?.id
    ) {
      return;
    }

    try {
      setLikeLoading(true);

      if (liked) {
        await socialService.unlikePost(
          post.id
        );

        setLiked(false);

        setLikeCount(
          (count) =>
            Math.max(
              0,
              count - 1
            )
        );
      } else {
        await socialService.likePost(
          post.id
        );

        setLiked(true);

        setLikeCount(
          (count) =>
            count + 1
        );
      }
    } catch (error) {
      console.error(
        "LIKE / UNLIKE ERROR:",
        error
      );
    } finally {
      setLikeLoading(false);
    }
  };

  // ============================================================
  // LOAD COMMENTS
  // ============================================================

  const loadComments = async () => {
    if (!post?.id) {
      return;
    }

    try {
      setCommentsLoading(true);
      setCommentError("");

      const response =
        await socialService.getComments(
          post.id,
          0,
          20
        );

      console.log(
        "COMMENTS RESPONSE:",
        response
      );

      const commentList =
        Array.isArray(
          response?.content
        )
          ? response.content
          : [];

      console.log(
        "COMMENT LIST:",
        commentList
      );

      setComments(
        commentList
      );

      if (
        typeof response?.totalElements ===
        "number"
      ) {
        setCommentCount(
          response.totalElements
        );
      }
    } catch (error) {
      console.error(
        "GET COMMENTS ERROR:",
        error
      );

      setCommentError(
        error?.message ||
          "Unable to load comments."
      );
    } finally {
      setCommentsLoading(false);
    }
  };

  // ============================================================
  // COMMENT BUTTON
  // ============================================================

  const handleCommentClick =
    async () => {
      const nextState =
        !showComments;

      setShowComments(
        nextState
      );

      if (nextState) {
        await loadComments();
      }
    };

  // ============================================================
  // ADD COMMENT
  // ============================================================

  const handleAddComment =
    async (event) => {
      event.preventDefault();

      const text =
        commentText.trim();

      if (
        !text ||
        !post?.id ||
        commentSubmitting
      ) {
        return;
      }

      try {
        setCommentSubmitting(
          true
        );

        setCommentError("");

        const newComment =
          await socialService.addComment(
            post.id,
            text
          );

        console.log(
          "NEW COMMENT RESPONSE:",
          newComment
        );

        if (newComment) {
          /*
           * Some backends return the newly
           * created comment without user data.
           *
           * If that happens and this is our
           * own comment, attach the current
           * logged-in user information.
           */

          const commentWithUser = {
            ...newComment,

            user:
              newComment?.user ||
              currentUser,

            userId:
              newComment?.userId ||
              currentUser?.id ||
              null,
          };

          setComments(
            (current) => [
              ...current,
              commentWithUser,
            ]
          );

          setCommentCount(
            (count) =>
              count + 1
          );
        } else {
          await loadComments();
        }

        setCommentText("");
      } catch (error) {
        console.error(
          "ADD COMMENT ERROR:",
          error
        );

        setCommentError(
          error?.message ||
            "Failed to add comment."
        );
      } finally {
        setCommentSubmitting(
          false
        );
      }
    };

  // ============================================================
  // DELETE POST
  // ============================================================

  const handleDeletePost =
    async () => {
      if (
        deleteLoading ||
        !post?.id
      ) {
        return;
      }

      try {
        setDeleteLoading(true);
        setDeleteError("");

        console.log(
          "DELETE POST:",
          post.id
        );

        await socialService.deletePost(
          post.id
        );

        console.log(
          "POST DELETED:",
          post.id
        );

        if (onPostDeleted) {
          onPostDeleted(
            post.id
          );
        }

        setShowDeleteConfirm(
          false
        );

        setShowMenu(false);
      } catch (error) {
        console.error(
          "DELETE POST ERROR:",
          error
        );

        setDeleteError(
          error?.message ||
            "Failed to delete post."
        );
      } finally {
        setDeleteLoading(
          false
        );
      }
    };

  // ============================================================
  // COMMENT DATE
  // ============================================================

  const formatCommentDate =
    (date) => {
      if (!date) {
        return "";
      }

      return new Date(
        date
      ).toLocaleString();
    };

  // ============================================================
  // RENDER
  // ============================================================

  return (
    <>
      <article
        className="
          bg-white
          border
          border-[#dfe7e2]
          rounded-3xl
          overflow-hidden
          shadow-xs
          relative
        "
      >

        {/* ====================================================
            HEADER
        ==================================================== */}

        <div
          className="
            p-5
            flex
            items-center
            gap-3
          "
        >
          <UserHeader
            user={postUser}
            createdAt={post?.createdAt}
            showFollow={!isMyPost}
            isFollowing={isFollowing}
            followLoading={followLoading}
            onFollow={handleFollow}
          />

          {/* FOLLOW ERROR */}

          {followError && (
            <p
              className="
                absolute
                right-5
                top-[72px]
                text-[9px]
                text-red-500
                max-w-[150px]
                text-right
              "
            >
              {followError}
            </p>
          )}

          {/* POST MENU */}

          {isMyPost && (
            <div className="relative">

              <button
                type="button"
                onClick={() =>
                  setShowMenu(
                    (value) =>
                      !value
                  )
                }
                className="
                  p-2
                  rounded-xl
                  hover:bg-[#f7faf8]
                  text-[#68756f]
                "
              >
                <MoreVertical
                  size={18}
                />
              </button>

              {showMenu && (
                <div
                  className="
                    absolute
                    right-0
                    top-10
                    z-20
                    w-36
                    bg-white
                    border
                    border-[#dfe7e2]
                    rounded-xl
                    shadow-lg
                    p-1
                  "
                >
                  <button
                    type="button"
                    onClick={() => {
                      setShowMenu(false);

                      setShowDeleteConfirm(
                        true
                      );
                    }}
                    className="
                      w-full
                      flex
                      items-center
                      gap-2
                      px-3
                      py-2.5
                      rounded-lg
                      text-xs
                      font-semibold
                      text-red-600
                      hover:bg-red-50
                    "
                  >
                    <Trash2
                      size={14}
                    />

                    Delete Post
                  </button>
                </div>
              )}
            </div>
          )}
        </div>

        {/* ====================================================
            CONTENT
        ==================================================== */}

        {post?.content && (
          <div className="px-5 pb-5">
            <p
              className="
                text-sm
                leading-6
                whitespace-pre-wrap
              "
            >
              {post.content}
            </p>
          </div>
        )}

        {/* ====================================================
            IMAGE
        ==================================================== */}

        {post?.imageUrl && (
          <div className="px-5 pb-5">
            <img
              src={post.imageUrl}
              alt="Post"
              className="
                w-full
                max-h-[500px]
                object-cover
                rounded-2xl
              "
              onError={(event) => {
                console.error(
                  "POST IMAGE FAILED:",
                  post.imageUrl
                );

                event.currentTarget.style.display =
                  "none";
              }}
            />
          </div>
        )}

        {/* ====================================================
            COUNTS
        ==================================================== */}

        <div
          className="
            px-5
            py-3
            border-t
            border-[#dfe7e2]
            flex
            justify-between
            text-xs
            text-[#68756f]
          "
        >
          <span>
            {likeCount}{" "}
            {likeCount === 1
              ? "like"
              : "likes"}
          </span>

          <span>
            {commentCount}{" "}
            {commentCount === 1
              ? "comment"
              : "comments"}
          </span>
        </div>

        {/* ====================================================
            ACTIONS
        ==================================================== */}

        <div
          className="
            px-5
            py-3
            border-t
            border-[#dfe7e2]
            flex
            gap-2
          "
        >
          <button
            type="button"
            disabled={likeLoading}
            onClick={handleLike}
            className={`
              flex-1
              flex
              items-center
              justify-center
              gap-2
              py-2.5
              rounded-2xl
              text-xs
              font-bold
              disabled:opacity-50

              ${
                liked
                  ? "bg-red-50 text-red-600"
                  : "text-[#68756f] hover:bg-[#f7faf8]"
              }
            `}
          >
            {likeLoading ? (
              <Loader2
                size={16}
                className="animate-spin"
              />
            ) : (
              <Heart
                size={16}
                fill={
                  liked
                    ? "currentColor"
                    : "none"
                }
              />
            )}

            {liked
              ? "Liked"
              : "Like"}
          </button>

          <button
            type="button"
            onClick={
              handleCommentClick
            }
            className={`
              flex-1
              flex
              items-center
              justify-center
              gap-2
              py-2.5
              rounded-2xl
              text-xs
              font-bold

              ${
                showComments
                  ? "bg-[#f7faf8] text-[#19714e]"
                  : "text-[#68756f] hover:bg-[#f7faf8]"
              }
            `}
          >
            <MessageCircle
              size={16}
            />

            Comment
          </button>
        </div>

        {/* ====================================================
            COMMENTS
        ==================================================== */}

        {showComments && (
          <div
            className="
              border-t
              border-[#dfe7e2]
              bg-[#fbfcfb]
            "
          >
            {/* COMMENTS HEADER */}

            <div
              className="
                px-5
                pt-4
                pb-3
                flex
                items-center
                justify-between
              "
            >
              <div>
                <p className="text-sm font-bold">
                  Comments
                </p>

                <p
                  className="
                    text-[11px]
                    text-[#68756f]
                  "
                >
                  {commentCount}{" "}
                  {commentCount === 1
                    ? "comment"
                    : "comments"}
                </p>
              </div>

              <button
                type="button"
                onClick={
                  loadComments
                }
                disabled={
                  commentsLoading
                }
                className="
                  p-2
                  rounded-xl
                  border
                  border-[#dfe7e2]
                  bg-white
                  text-[#68756f]
                "
              >
                {commentsLoading ? (
                  <Loader2
                    size={14}
                    className="animate-spin"
                  />
                ) : (
                  <RefreshCw
                    size={14}
                  />
                )}
              </button>
            </div>

            {/* COMMENT ERROR */}

            {commentError && (
              <div
                className="
                  mx-5
                  mb-3
                  px-3
                  py-2.5
                  rounded-xl
                  bg-red-50
                  border
                  border-red-200
                  text-red-600
                  text-xs
                  font-semibold
                "
              >
                {commentError}
              </div>
            )}

            {/* COMMENT LIST */}

            <div
              className="
                px-5
                space-y-3
                max-h-[350px]
                overflow-y-auto
              "
            >
              {commentsLoading &&
              comments.length === 0 ? (
                <div
                  className="
                    py-6
                    flex
                    items-center
                    justify-center
                    gap-2
                    text-xs
                    text-[#68756f]
                  "
                >
                  <Loader2
                    size={15}
                    className="animate-spin"
                  />

                  Loading comments...
                </div>
              ) : comments.length === 0 ? (
                <div
                  className="
                    py-6
                    text-center
                  "
                >
                  <MessageCircle
                    size={22}
                    className="
                      mx-auto
                      text-[#68756f]
                    "
                  />

                  <p
                    className="
                      text-xs
                      font-semibold
                      text-[#68756f]
                      mt-2
                    "
                  >
                    No comments yet
                  </p>
                </div>
              ) : (
                comments.map(
                  (comment) => {

                    // ========================================
                    // FIND COMMENT USER
                    // ========================================

                    const commentUser =
                      findUserForComment(
                        comment,
                        users,
                        currentUser
                      );

                    const commentUsername =
                      commentUser?.username ||
                      commentUser?.name ||
                      "SkillCart User";

                    const commentInitial =
                      commentUsername
                        .trim()
                        .charAt(0)
                        .toUpperCase() ||
                      "U";

                    console.log(
                      "COMMENT USER DATA:",
                      {
                        commentId:
                          comment?.id,

                        commentUserId:
                          comment?.userId,

                        commentUser,

                        commentUsername,

                        currentUser,

                        usersCount:
                          users?.length ||
                          0,
                      }
                    );

                    return (
                      <div
                        key={
                          comment.id
                        }
                        className="
                          flex
                          gap-3
                        "
                      >

                        {/* COMMENT AVATAR */}

                        <div
                          className="
                            w-8
                            h-8
                            rounded-xl
                            bg-gradient-to-br
                            from-[#123c2c]
                            to-[#19714e]
                            text-[#b9ef84]
                            flex
                            items-center
                            justify-center
                            text-[10px]
                            font-bold
                            shrink-0
                          "
                        >
                          {commentInitial}
                        </div>

                        {/* COMMENT CONTENT */}

                        <div
                          className="
                            flex-1
                            min-w-0
                          "
                        >
                          <div
                            className="
                              bg-white
                              border
                              border-[#dfe7e2]
                              rounded-2xl
                              px-3.5
                              py-2.5
                            "
                          >

                            {/* USERNAME */}

                            <p
                              className="
                                text-xs
                                font-bold
                                text-[#12221d]
                              "
                            >
                              {
                                commentUsername
                              }
                            </p>

                            {/* COMMENT */}

                            <p
                              className="
                                text-xs
                                text-[#12221d]
                                leading-5
                                mt-1
                                whitespace-pre-wrap
                                break-words
                              "
                            >
                              {
                                comment.content
                              }
                            </p>
                          </div>

                          {/* DATE */}

                          <p
                            className="
                              text-[10px]
                              text-[#68756f]
                              mt-1
                              ml-1
                            "
                          >
                            {formatCommentDate(
                              comment.createdAt
                            )}
                          </p>
                        </div>
                      </div>
                    );
                  }
                )
              )}
            </div>

            {/* ==================================================
                ADD COMMENT
            ================================================== */}

            <form
              onSubmit={
                handleAddComment
              }
              className="
                p-5
                mt-2
                border-t
                border-[#dfe7e2]
                flex
                gap-2
              "
            >
              <input
                type="text"
                value={commentText}
                onChange={(event) =>
                  setCommentText(
                    event.target.value
                  )
                }
                placeholder="Write a comment..."
                disabled={
                  commentSubmitting
                }
                maxLength={1000}
                className="
                  flex-1
                  min-w-0
                  px-3.5
                  py-2.5
                  rounded-xl
                  bg-white
                  border
                  border-[#dfe7e2]
                  text-xs
                  outline-none
                  focus:border-[#19714e]
                  focus:ring-2
                  focus:ring-[#19714e]/20
                "
              />

              <button
                type="submit"
                disabled={
                  commentSubmitting ||
                  !commentText.trim()
                }
                className="
                  w-10
                  h-10
                  rounded-xl
                  bg-[#123c2c]
                  hover:bg-[#19714e]
                  text-white
                  flex
                  items-center
                  justify-center
                  shrink-0
                  disabled:opacity-50
                "
              >
                {commentSubmitting ? (
                  <Loader2
                    size={15}
                    className="animate-spin"
                  />
                ) : (
                  <Send
                    size={15}
                  />
                )}
              </button>
            </form>
          </div>
        )}
      </article>

      {/* ======================================================
          DELETE CONFIRMATION
      ====================================================== */}

      {showDeleteConfirm && (
        <div
          className="
            fixed
            inset-0
            z-[100]
            bg-black/40
            backdrop-blur-sm
            flex
            items-center
            justify-center
            p-4
          "
        >
          <div
            className="
              w-full
              max-w-sm
              bg-white
              rounded-2xl
              p-5
              shadow-2xl
            "
          >
            <div
              className="
                flex
                items-center
                justify-between
                mb-4
              "
            >
              <h3
                className="
                  text-sm
                  font-bold
                  text-[#12221d]
                "
              >
                Delete Post
              </h3>

              <button
                type="button"
                onClick={() =>
                  setShowDeleteConfirm(
                    false
                  )
                }
                className="
                  p-1.5
                  rounded-lg
                  hover:bg-[#f7faf8]
                  text-[#68756f]
                "
              >
                <X size={16} />
              </button>
            </div>

            <p
              className="
                text-xs
                leading-5
                text-[#68756f]
              "
            >
              Are you sure you want to
              delete this post? This
              action cannot be undone.
            </p>

            {deleteError && (
              <div
                className="
                  mt-3
                  px-3
                  py-2.5
                  rounded-xl
                  bg-red-50
                  border
                  border-red-200
                  text-red-600
                  text-xs
                  font-semibold
                "
              >
                {deleteError}
              </div>
            )}

            <div
              className="
                mt-5
                flex
                gap-2
              "
            >
              <button
                type="button"
                onClick={() =>
                  setShowDeleteConfirm(
                    false
                  )
                }
                disabled={
                  deleteLoading
                }
                className="
                  flex-1
                  py-2.5
                  rounded-xl
                  border
                  border-[#dfe7e2]
                  text-xs
                  font-bold
                  text-[#68756f]
                  hover:bg-[#f7faf8]
                "
              >
                Cancel
              </button>

              <button
                type="button"
                onClick={
                  handleDeletePost
                }
                disabled={
                  deleteLoading
                }
                className="
                  flex-1
                  py-2.5
                  rounded-xl
                  bg-red-600
                  hover:bg-red-700
                  text-white
                  text-xs
                  font-bold
                  flex
                  items-center
                  justify-center
                  gap-2
                  disabled:opacity-50
                "
              >
                {deleteLoading ? (
                  <>
                    <Loader2
                      size={14}
                      className="animate-spin"
                    />

                    Deleting...
                  </>
                ) : (
                  <>
                    <Trash2
                      size={14}
                    />

                    Delete
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}