const Joi = require('joi')


// Register validation
const registerValidation = data => {
    const schema = Joi.object({
        // username: Joi.string().min(5).max(24).regex(/^[A-Za-z0-9_.]+$/).required(),
        password: Joi.string().min(6).max(48).required(),
        nickname: Joi.string().min(3).max(100).required(),
        email: Joi.string().min(10).max(48).email().required(),
    });

    return schema.validate(data);
};

// chnge username validation
const changeusernameValidation = data => {
    const schema = Joi.object({
        username: Joi.string().min(4).max(32).regex(/^[A-Za-z0-9_.]+$/).required(),
    });

    return schema.validate(data);
};

// Login validation
const loginValidation = data => {
    const schema = Joi.object({
        username: Joi.string().min(5).required(),
        password: Joi.string().min(6).required(),
    });
    return schema.validate(data);
};


// change password validation
const changepasswordValidation = data => {
    const schema = Joi.object({
        password: Joi.string().min(6).required(),
        newpassword: Joi.string().min(6).max(64).required(),
        username: Joi.string().min(5).required(),
    });
    return schema.validate(data);
};

module.exports.registerValidation = registerValidation
module.exports.loginValidation = loginValidation
module.exports.changepasswordValidation = changepasswordValidation
module.exports.changeusernameValidation = changeusernameValidation