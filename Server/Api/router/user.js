const router = require("express").Router();
const User = require("../models/user");
const bcrypt = require("bcrypt");
// const rateLimit = require('express-rate-limit')


const {
    registerValidation,
    loginValidation,
    changepasswordValidation,
    changeusernameValidation
} = require("./validations");

const jwt = require('jsonwebtoken');
const randomstring = require("randomstring");
const auth = require('./verifyToken');
const env = require('dotenv').config();
const mongoose = require('mongoose')

// Limit for requests
/* const generalRequestLimit = rateLimit({
    windowMs: 3 * 1000, // 60 sec in milliseconds
    max: 1,
    message: {
        success: false,
        error: 'You have exceeded the max requests limit! try later.'
    },
    headers: true,
});
 */


// change password
router.post('/changepass', auth, async (req, res) => {
    try {
        // Validate data before save to db
        const {
            error
        } = changepasswordValidation(req.body);
        if (error) {
            return res.json({
                error: error.details[0].message,
                success: false
            });
        }
        const user = await User.findOne({
            username: req.body.username,
        });

        if (!user) {
            return res.json({
                success: false,
                error: "Wrong credentials.",
            });
        }

        const validated = await bcrypt.compare(req.body.password, user.password);
        if (!validated) {
            return res.json({
                success: false,
                error: "Wrong credentials.",
            });
        }

        const token = jwt.sign({
            id: user._id,
            email: user.email,
            username: user.username,
        },
            process.env.jwt_secret, {
            expiresIn: "1d"
        })

        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(req.body.newpassword, salt);
        const update = await User.findOneAndUpdate({
            username: req.body.username
        }, {
            password: hashedPassword
        })

        res.header("auth-token", token).json({
            success: true,
            _id: update._id,
            error: null,
            token: token,
        });

    } catch (error) {
        res.json({
            error: 'Server error',
            success: false
        })
    }
});

// search username
router.post('/searchusername', auth, async (req, res) => {
    try {
        // Validate data before save to db
        const {
            error
        } = changeusernameValidation(req.body);
        if (error) {
            return res.json({
                error: error.details[0].message,
                success: false
            });
        }

        const user = await User.find({ "username": { "$regex": req.body.username, "$options": "i" } }).select({ __v: 0, password: 0 });

        return res.json({
            users: user
        })

    }
    catch (err) {
        res.json({
            error: err,
            success: false,
            code: 500
        });
    }
})

// get users info by ids
router.post('/getInfoByIds', auth, async (req, res) => {
    try {
        const ids = req.body.map((f) => f)
        const userInfo = await User.find({ _id: { $in: ids.map(mongoose.Types.ObjectId) } }).select({ __v: 0, password: 0 })
        res.json(userInfo)
    }
    catch (err) {
        res.json({
            error: err,
            success: false,
            code: 500
        });
    }
})

// get user info by token
router.post('/getuserinfo', auth, async (req, res) => {
    try {
        const user = await User.findOne({ email: req.user.email }).select({ __v: 0, password: 0 })
        if (!user) {
            return res.json({
                error: '',
                message: 'username not found',
                success: false,
            });
        } else {

            return res.json({
                error: '',
                userinfo: user,
                success: true,
            });
        }
    }
    catch (err) {
        res.json({
            error: err,
            success: false,
            code: 500
        });
    }
})

// save username
router.post('/changeusername', auth, async (req, res) => {
    try {
        //  Validate data before save to db
        const {
            error
        } = changeusernameValidation(req.body);
        if (error) {
            return res.json({
                error: error.details[0].message,
                success: false,
                code: 401
            });
        }

        const user = await User.findOne({ username: req.body.username })

        if (!user) {
            await User.findOneAndUpdate({ email: req.user.email }, { username: req.body.username })

            return res.json({
                error: '',
                message: 'username changed',
                success: true,
            });

        } else {
            return res.json({
                error: '',
                message: 'username taken please choose diffrent username',
                success: false,
            });
        }


    } catch (err) {
        res.json({
            error: err,
            success: false,
            code: 500
        });
    }
});

// register User
router.post("/register", async (req, res) => {
    try {

        // Validate data before save to db
        const {
            error
        } = registerValidation(req.body);
        if (error) {
            return res.json({
                error: error.details[0].message,
                success: false,
                code: 401
            });
        }

        /* const user = await User.findOne({
            $or: [{
                username: req.body.username
            }, {
                email: req.body.email
            }]
        }) */

        const user = await User.findOne({ email: req.body.email })

        let er = ""
        if (user) {
            if (user.email === req.body.email) {
                er = "Email already exist"
            }

            return res.json({
                error: er,
                success: false,
                code: 402
            })
        } else {
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash(req.body.password, salt);

            const newUser = new User({
                username: req.body.username,
                email: req.body.email,
                nickname: req.body.nickname,
                password: hashedPassword,
                avatar: `https://qbeesoft.com/uploads/avatar.png`
            });

            const savedUser = await newUser.save();

            // generate secret token
            const token = jwt.sign({
                id: savedUser._doc._id,
                email: savedUser._doc.email,
                nickname: savedUser._doc.nickname
            },
                process.env.jwt_secret, {
                expiresIn: "1d" // 1day will be expaire
            })

            const userInfo = {
                id: savedUser._doc._id,
                email: newUser.email,
                nickname: newUser.nickname,
                avatar: newUser.avatar
            }

            res.json({
                error: 'null',
                message: 'user registered successfully',
                user: userInfo,
                success: true,
                token: token,
                code: 200
            });

        }

    } catch (err) {
        res.json({
            error: err,
            success: false,
            code: 500
        });
    }
});

// refreshe json web token
router.post('/refreshtoken', async (req, res) => {
    try {

        const token = req.header("auth-token");
        if (!token) return res.send({
            success: false,
            error: 'Access Denied - Authorization failed'
        })

        const verified = jwt.verify(token, process.env.jwt_secret)

        // generate new secret token
        const newToken = jwt.sign({
            id: verified.id,
            email: verified.email,
            username: verified.username
        },
            process.env.jwt_secret, {
            expiresIn: "1d" // 1day will be expaire
        })
        return res.json({ token: newToken, success: true })

    } catch (err) {
        res.json({
            error: err,
            success: false
        });
    }
});

// login
router.post("/login", async (req, res) => {
    try {

        // Validate data before login
        const {
            error
        } = loginValidation(req.body);
        if (error) {
            return res.status(401).json({
                error: error.details[0].message,
                success: false
            });
        }

        /* const user = await User.findOne({
            username: req.body.username
        }); */

        // check username or email exist
        const user = await User.findOne({ $or: [{ username: req.body.username }, { email: req.body.username }] })

        if (user) {

            // check the user password
            const validated = await bcrypt.compare(req.body.password, user.password);
            if (!validated) {
                return res.json({
                    success: false,
                    error: "Wrong credentials.",
                });
            }

            // generate secret token
            const token = jwt.sign({
                id: user._id,
                email: user.email,
                username: user.username
            },
                process.env.jwt_secret, {
                expiresIn: "1d" // 1day will be expaire
            })

            const userInfo = {
                id: user._id,
                email: user.email,
                username: user.username,
                nickname: user.nickname,
                avatar: user.avatar
            }
            return res.json({
                success: true,
                error: null,
                user: userInfo,
                token: token,
            });

        }

        else {
            return res.json({
                success: false,
                error: "user not exist"
            })
        }

    } catch (err) {
        res.json({
            error: err,
            success: false
        });
    }
});


module.exports = router;
