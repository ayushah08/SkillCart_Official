import socialApi from "./socialApi";

const socialService = {

  // ============================================================
  // FOLLOW USER
  // ============================================================

  followUser: (userId) =>
    socialApi.post(
      `/api/social/users/${userId}/follow`
    ),

  // ============================================================
  // UNFOLLOW USER
  // ============================================================

  unfollowUser: (userId) =>
    socialApi.delete(
      `/api/social/users/${userId}/follow`
    ),

  // ============================================================
  // CHECK FOLLOWING STATUS
  // ============================================================

  getFollowingStatus: (userId) =>
    socialApi.get(
      `/api/social/users/${userId}/following-status`
    ),

  // ============================================================
  // GET HOME FEED
  // ============================================================

  getFeed: (
    page = 0,
    size = 20
  ) =>
    socialApi.get(
      `/api/social/feed?page=${page}&size=${size}`
    ),

  // ============================================================
  // GET SINGLE POST
  // ============================================================

  getPost: (postId) =>
    socialApi.get(
      `/api/social/posts/${postId}`
    ),

  // ============================================================
  // GET USER POSTS
  // ============================================================

  getUserPosts: (
    userId,
    page = 0,
    size = 50
  ) =>
    socialApi.get(
      `/api/social/posts/user/${userId}?page=${page}&size=${size}`
    ),

  // ============================================================
  // CREATE POST
  // ============================================================

  createPost: ({
    content,
    image,
  }) => {

    const formData =
      new FormData();

    if (
      content &&
      content.trim()
    ) {
      formData.append(
        "content",
        content.trim()
      );
    }

    if (image) {
      formData.append(
        "image",
        image
      );
    }

    return socialApi.post(
      "/api/social/posts",
      formData
    );
  },

  // ============================================================
  // GET ALL USERS
  // ============================================================

  getAllUsers: async () => {

    const token =
      localStorage.getItem(
        "token"
      );

    const response =
      await fetch(
        "https://skillcart-auth.onrender.com/api/users",
        {
          method: "GET",

          headers: {
            ...(token
              ? {
                  Authorization:
                    `Bearer ${token}`,
                }
              : {}),
          },
        }
      );

    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
          `Failed to fetch users (${response.status})`
      );
    }

    const data =
      await response.json();

    console.log(
      "RAW USERS API RESPONSE:",
      data
    );

    // ========================================================
    // IMPORTANT
    // Backend may return:
    //
    // 1. [ user1, user2 ]
    //
    // OR
    //
    // 2. { content: [ user1, user2 ] }
    //
    // OR
    //
    // 3. { data: [ user1, user2 ] }
    //
    // Normalize everything to an array.
    // ========================================================

    if (Array.isArray(data)) {
      return data;
    }

    if (Array.isArray(data?.content)) {
      return data.content;
    }

    if (Array.isArray(data?.data)) {
      return data.data;
    }

    if (
      Array.isArray(
        data?.data?.content
      )
    ) {
      return data.data.content;
    }

    console.warn(
      "USERS API DID NOT RETURN AN ARRAY:",
      data
    );

    return [];
  },

  // ============================================================
  // FOLLOWERS COUNT
  // ============================================================

  getFollowersCount: (
    userId
  ) =>
    socialApi.get(
      `/api/social/users/${userId}/followers/count`
    ),

  // ============================================================
  // FOLLOWING COUNT
  // ============================================================

  getFollowingCount: (
    userId
  ) =>
    socialApi.get(
      `/api/social/users/${userId}/following/count`
    ),

  // ============================================================
  // LIKE POST
  // ============================================================

  likePost: (postId) =>
    socialApi.post(
      `/api/social/posts/${postId}/like`
    ),

  // ============================================================
  // UNLIKE POST
  // ============================================================

  unlikePost: (postId) =>
    socialApi.delete(
      `/api/social/posts/${postId}/like`
    ),

  // ============================================================
  // GET COMMENTS
  // ============================================================

  getComments: (
    postId,
    page = 0,
    size = 20
  ) =>
    socialApi.get(
      `/api/social/comments/post/${postId}?page=${page}&size=${size}`
    ),

  // ============================================================
  // ADD COMMENT
  // ============================================================

  addComment: (
    postId,
    content
  ) =>
    socialApi.post(
      `/api/social/comments/post/${postId}`,
      {
        content:
          content.trim(),
      }
    ),

  // ============================================================
  // DELETE COMMENT
  // ============================================================

  deleteComment: (
    commentId
  ) =>
    socialApi.delete(
      `/api/social/comments/${commentId}`
    ),

  // ============================================================
  // DELETE POST
  // ============================================================

  deletePost: (
    postId
  ) =>
    socialApi.delete(
      `/api/social/posts/${postId}`
    ),
};

export default socialService;