<?php
header("Content-Type: application/json");

function getLyrics($key)
{
    $param = urlencode($key . " (Lyrics)");
    $context = stream_context_create([
        "http" => [
            "header" => [
                "user-agent: " .
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36",
            ],
            "ignore_errors" => true,
        ],
    ]);
    $res = file_get_contents(
        "https://www.google.com/search?hl=en&q=$param",
        false,
        $context
    );

    $re = '/data-lyricid=".*?"><div class=((.|\n)*?)<div class=/m';
    preg_match($re, $res, $matches, PREG_OFFSET_CAPTURE, 0);

    $lyrics = strip_tags(
        "<div class=" .
            str_replace(["<br>", "<br />", "<br/>"], "\n", $matches[1][0])
    );

    $re = '/data-attrid="title"((.|\n)*?)<\/div>/m';
    preg_match($re, $res, $matches, PREG_OFFSET_CAPTURE, 0);
    $title = strip_tags("<div " . $matches[1][0] . "</div>");

    $re = '/data-attrid="subtitle"((.|\n)*?)<\/div>/m';
    preg_match($re, $res, $matches, PREG_OFFSET_CAPTURE, 0);
    $subtitle = strip_tags("<div " . $matches[1][0] . "</div>");

    return "$title\n$subtitle\n\n$lyrics";
}

$data = json_decode(file_get_contents("php://input"));

if ($data->isPrivateMessage) {
    echo json_encode([
        "to" => $data->chatId,
        "text" => getLyrics($data->message->text),
    ]);
} elseif (strpos($data->message->text, "/lyrics ") !== false) {
    echo json_encode([
        "to" => $data->chatId,
        "text" => getLyrics(
            trim(str_replace("/lyrics ", "", $data->message->text))
        ),
    ]);
}

?>
