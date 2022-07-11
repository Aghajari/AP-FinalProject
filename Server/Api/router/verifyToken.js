const jwt = require('jsonwebtoken');

module.exports = function (req, res, next) {
    const token = req.header("auth-token");

    if (!token) return res.send({
        success: false,
        error: 'Access Denied - Authorization failed'
    })

    try {
        const verified = jwt.verify(token, process.env.jwt_secret)
        req.user = verified;
        next();

    } catch (err) {
        res.status(400).send({
            error: 'Invalid Token'
        })
    }
}
