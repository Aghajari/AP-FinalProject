<?php
header("Content-Type: application/json");

define("FOLDER", "CrocodileGame");
define("CODE", "CROCODILE-SERVER-INVITE-CODE");

function generateRandomWord()
{
    $route = ["noun", "adjective", "verb"];
    $js = file_get_contents(
        "https://random-words-api.vercel.app/word/" . $route[array_rand($route)]
    );
    return json_decode($js)[0]->word;
}

$data = json_decode(file_get_contents("php://input"));

if ($data->isPrivateMessage) {
    echo json_encode([
        "to" => $data->chatId,
        "text" =>
            "Join our server to play game!\n" .
            CODE .
            "\n\nCommands:\n/start@game\n/stop@game",
    ]);
} else {
    $text = $data->message->text;

    mkdir(FOLDER);
    $file = FOLDER . "/" . $data->chatId . ".json";
    $js = file_get_contents("$file");

    if ($js === false) {
        $js = "{}";
    }

    $js = json_decode($js);

    if (!strcasecmp($text, "/start@game")) {
        if (isset($js->word)) {
            echo json_encode([
                "to" => $data->chatId,
                "text" => "Do not blabber. The game has already started :)",
            ]);
        } else {
            $word = strtolower(generateRandomWord());

            file_put_contents(
                $file,
                json_encode([
                    "word" => $word,
                    "leader" => $data->message->fromId,
                ])
            );

            echo json_encode([
                [
                    "to" => $data->chatId,
                    "text" =>
                        "Game Started.\n" .
                        $data->from->nickname .
                        " is explaining the word!",
                ],
                [
                    "to" => $data->message->fromId,
                    "text" =>
                        "Word: $word" .
                        "\nServerName: " .
                        $data->server->name .
                        "\nChannel: " .
                        $data->channel->name
                ],
            ]);
        }
    } elseif (!strcasecmp($text, "/stop@game")) {
        if (isset($js->word)) {
            file_put_contents($file, "{}");
            echo json_encode([
                "to" => $data->chatId,
                "text" =>
                    "The game is stopped by order of " .
                    $data->from->nickname .
                    ",\nThe answer was " .
                    $js->word .
                    ".",
            ]);
        } else {
            echo json_encode([
                "to" => $data->chatId,
                "text" => "No game was available!",
            ]);
        }
    } elseif (
        isset($js->word) and
        $js->leader != $data->message->fromId and
        !strcasecmp($js->word, $text)
    ) {
        file_put_contents($file, "{}");
        echo json_encode([
            "to" => $data->chatId,
            "text" => $data->from->nickname . " found the word! " . $js->word,
        ]);
    }
}

?>
