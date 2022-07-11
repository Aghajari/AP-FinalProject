const express = require("express");
const app = express();
const userRouter = require("./router/user");
const path = require('path');
const dotenv = require("dotenv").config();
const mongoose = require("mongoose");
const User = require("./models/user");
const multer = require("multer");
const singleImgFileDir = '../../Uploads/Apps/Discord/avatars'
const auth = require('./router/verifyToken');
const randomstring = require("randomstring");
const port = process.env.port || 8080;

function getTime() {
    return parseInt(new Date().getTime() + 12610000);
}

// Conect TO db
mongoose
    .connect(process.env.connectionstring, {
        useNewUrlParser: true,
        useUnifiedTopology: true
    })
    .then(console.log("Connected"))
    .catch(err => console.log(err))


// Route Middlewares
app.use(express.json());
app.use('/public', express.static(path.join(__dirname, 'public')));

const singleStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, singleImgFileDir);
    },
    filename: function (req, file, cb) {
        var fileExtention = file.originalname.split('.').pop();
        cb(null, req.user.id + '.' + fileExtention);
    },
});

// init diskstorage for upload group image
const singleFileForGroupStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, singleImgFileDir);
    },
    filename: function (req, file, cb) {
        var fileExtention = file.originalname.split('.').pop();
        cb(null, req.header('filename'));
    },
});

// init diskstorage for upload avatar image
const upload = multer({
    storage: singleStorage,
    limits: {
        fileSize: 500000
    }, // 500 kb
    fileFilter: (req, file, cb) => {
        checkFileType(file, cb);
    }
}).single("image");

// group image upload
const uploadForGroup = multer({
    storage: singleFileForGroupStorage,
    limits: {
        fileSize: 500000
    }, // 500 kb
    fileFilter: (req, file, cb) => {
        checkFileType(file, cb);
    }
}).single("image");

// Check FileType
function checkFileType(file, cb) {
    const filetypes = /jpg|jpeg|png|webp/;
    const extname = filetypes.test(path.extname(file.originalname).toLocaleLowerCase());
    // check mime
    const mimetype = filetypes.test(file.mimetype);
    if (mimetype && extname) {
        return cb(null, true)
    } else {
        cb('You can uploading images only!')
    }
}

app.post("/uploadimage", auth, async (req, res) => {
    try {

        const Upload = await uploadForGroup(req, res, (err) => {
            if (err) {
                return res.json({
                    error: err,
                    success: false
                });
            } else {

                if (req.file === undefined) {
                    return res.json({
                        success: false,
                        error: 'No file selected'
                    })
                }

                var fileaddress = `https://YOUR-DOMAIN.com/Uploads/Apps/Discord/avatars/${req.header('filename')}`

                return res.json({
                    error: null,
                    url: fileaddress,
                    success: true,
                });
            }
        })
    }
    catch (error) {
        res.json({
            success: false,
            error: error
        })
    }
})

app.post("/uploadAvatar", auth, async (req, res) => {
    try {

        const Upload = await upload(req, res, (err) => {

            if (err) {
                return res.json({
                    error: err,
                    success: false
                });
            } else {

                if (req.file === undefined) {
                    return res.json({
                        success: false,
                        error: 'No file selected'
                    })
                }

                var fileExtention = req.file.originalname.split('.').pop();
                var randomName = randomstring.generate({
                    length: 8,
                    charset: '0123456789qwertyuiopasdfghjklzxcvbnm'
                })

                var fileaddress = `https://YOUR-DOMAIN.com/Uploads/Apps/Discord/avatars/${req.user.id}.${fileExtention}?id=${getTime()}`

                User.findOneAndUpdate({ _id: req.user.id }, { avatar: fileaddress }, async (err, cb) => {
                    if (err) {
                        res.json({
                            error: err,
                            success: false,
                        });
                    }

                    return res.json({
                        error: null,
                        url: fileaddress,
                        success: true,
                    });
                })
            }
        });
    } catch (error) {
        res.json({
            success: false,
            error: error
        })
    }

});


// user api requests
app.use("/api/user", userRouter)

const http = require("http").createServer(app);

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, '/index.html'));
});

// listen to port and start server
http.listen(port, () => {
    console.log(
        `Server runing on port ${port} at: ${getTime()}`);
});