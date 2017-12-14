package io.zjw.rxdemo.gson;

import java.util.List;

/**
 * Created by mega on 2017/12/14.
 */

public class RandomUserResults {
    public List<UserInfo> results;

    public class UserInfo {
        public UserName name;
        public String gender;
        public UserLocation location;
        public String email;
        public String dob;
        public UserPicture picture;

        public class UserName {
            public String title;
            public String first;
            public String last;

            @Override
            public String toString() {
                return "UserName{" +
                        "title='" + title + '\'' +
                        ", first='" + first + '\'' +
                        ", last='" + last + '\'' +
                        '}';
            }
        }

        public class UserLocation {
            public String street;
            public String city;
            public String state;
            public int postcode;

            @Override
            public String toString() {
                return "UserLocation{" +
                        "street='" + street + '\'' +
                        ", city='" + city + '\'' +
                        ", state='" + state + '\'' +
                        ", postcode=" + postcode +
                        '}';
            }
        }

        public class UserPicture {
            public String large;
            public String medium;
            public String thumbnail;

            @Override
            public String toString() {
                return "UserPicture{" +
                        "large='" + large + '\'' +
                        ", medium='" + medium + '\'' +
                        ", thumbnail='" + thumbnail + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "name=" + name +
                    ", gender='" + gender + '\'' +
                    ", location=" + location +
                    ", email='" + email + '\'' +
                    ", dob='" + dob + '\'' +
                    ", picture=" + picture +
                    '}';
        }
    }
}
