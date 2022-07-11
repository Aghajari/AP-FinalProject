const { string } = require("joi");
const mongoose = require("mongoose");

function getTime() {
    return new Date().getTime();
}

const userSchema = new mongoose.Schema({
    username: {
        type: String,
        require: false,
        min: 5,
        max: 64,
    },
    nickname: {
        type: String,
        required: true,
    },
    email: {
        type: String,
        require: true,
        min: 10,
        max: 100,
    },
    password: {
        type: String,
        require: true,
    },
    avatar: {
        type: String,
        require: false,
    }
});

module.exports = mongoose.model("user", userSchema);
