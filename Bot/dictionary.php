<?php
header("Content-Type: application/json");

$data = json_decode(file_get_contents('php://input'));
$text = $data->message->text;

if (strpos($text, "/define ") !== false)
{
    $text = str_replace('/define ', '', $text);
    $text_url = rawurlencode($text);
    $get = file_get_contents("https://dictionary.cambridge.org/dictionary/english/$text_url");

    $re_type = '/<div class="posgram dpos-g hdib lmr-5">((.|\n)*?)<\/div>/m';
    preg_match_all($re_type, $get, $match);
    $type_value = strip_tags($match[1][0]);

    $re_per = '/<span class="pron dpron">((.|\n)*?)<\/span>/m';
    preg_match_all($re_per, $get, $match);
    $per_uk = strip_tags($match[1][0]) . "/";
    $per_us = strip_tags($match[1][1]) . "/";

    $re_def = '/<div class="def ddef_d db">((.|\n)*?)<\/div>/m';
    preg_match_all($re_def, $get, $match);
    $def = trim(strip_tags($match[1][0]));
    $def = str_replace(array(
        "\r\n",
        "\n",
        "\r"
    ) , ' ', $def);
    if (substr($def, -1) == ':')
    {
        $def = substr($def, 0, strlen($def) - 1);
    }

    $re_ex = '/<div class="def-body ddef_b">((.|\n)*?)<\/div><\/div>/m';
    preg_match_all($re_ex, $get, $match2);

    $re_ex = '/<div class="examp dexamp">((.|\n)*?)<\/div>/m';
    preg_match_all($re_ex, $match2[1][0], $match2);

    foreach ($match2[1] as $dif)
    {
        $od = trim(strip_tags($dif));

        $re_index++;
        $example .= "$re_index. ";

        $example .= $od;
        $example .= "\r\n";
    }

    $example = trim($example);

    if (strlen($example) > 0)
    {
        echo json_encode(["to" => $data->chatId, "text" => "✍️ $text :$type_value
🗣 UK: $per_uk
🗣 US: $per_us

📖 Definition :
$def

📖 Examples :
$example"]);
    }
    else
    {
        echo json_encode(["to" => $data->chatId, "text" => "✍️ $text :$type_value
🗣 UK: $per_uk
🗣 US: $per_us

📖 Definition :
$def"]);
    }
}

?>
