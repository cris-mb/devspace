use cristian_db;

create table app_user(tagId VARCHAR(30) primary key NOT NULL, username VARCHAR(50) NOT NULL, email VARCHAR(50) NOT NULL, password VARCHAR(30) NOT NULL, about VARCHAR(150));
create table post(postId BIGINT primary key AUTO_INCREMENT NOT NULL, tagId_FK VARCHAR(30) NOT NULL, multimediaURL VARCHAR(255) NOT NULL, description VARCHAR(250), likes BIGINT, post_date DATE, codeURL VARCHAR(255), foreign key (tagId_FK) references app_user(tagId));
create table comment(commentId BIGINT primary key AUTO_INCREMENT NOT NULL, postId_FK BIGINT NOT NULL, tagId_FK VARCHAR(30) NOT NULL, message VARCHAR(100) NOT NULL, comment_date DATE NOT NULL, foreign key (postId_FK) references post(postId), foreign key (tagId_FK) references app_user(tagId));

create table follows(tagId_Followed VARCHAR(30) NOT NULL, tagId_Follower VARCHAR(30) NOT NULL, foreign key (tagId_Followed) references app_user(tagId), foreign key (tagId_Follower) references app_user(tagId));
create table describes(commentId_Comments BIGINT NOT NULL, commentId_Commented BIGINT NOT NULL, foreign key (commentId_Comments) references comment(commentId), foreign key (commentId_Commented) references comment(commentId));