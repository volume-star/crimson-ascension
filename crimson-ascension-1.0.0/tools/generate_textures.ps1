Add-Type -AssemblyName System.Drawing

$projectRoot = Split-Path -Parent $PSScriptRoot
$assetRoot = Join-Path $projectRoot "src\main\resources\assets\crimson_ascension\textures"
$itemRoot = Join-Path $assetRoot "item"
$blockRoot = Join-Path $assetRoot "block"
$effectRoot = Join-Path $assetRoot "mob_effect"

foreach ($directory in @($itemRoot, $blockRoot, $effectRoot)) {
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
}

function New-Canvas([int]$size) {
    return [System.Drawing.Bitmap]::new($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Set-Pixel($bitmap, [int]$x, [int]$y, [string]$color) {
    $bitmap.SetPixel($x, $y, [System.Drawing.ColorTranslator]::FromHtml($color))
}

function Fill-Rect($bitmap, [int]$x, [int]$y, [int]$width, [int]$height, [string]$color) {
    for ($py = $y; $py -lt ($y + $height); $py++) {
        for ($px = $x; $px -lt ($x + $width); $px++) {
            Set-Pixel $bitmap $px $py $color
        }
    }
}

function Save-Asset($bitmap, [string]$path) {
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bitmap.Dispose()
}

$dagger = New-Canvas 16
for ($i = 0; $i -lt 8; $i++) {
    Set-Pixel $dagger (11 - $i) (2 + $i) "#D9DDE2"
    Set-Pixel $dagger (12 - $i) (2 + $i) "#666E78"
    if ($i -ge 4) {
        Set-Pixel $dagger (13 - $i) (3 + $i) "#7C111E"
    }
}
Set-Pixel $dagger 4 9 "#8F98A2"
Set-Pixel $dagger 5 10 "#8F98A2"
Set-Pixel $dagger 3 7 "#B51B2E"
Set-Pixel $dagger 4 7 "#B51B2E"
Set-Pixel $dagger 5 8 "#B51B2E"
Set-Pixel $dagger 6 9 "#B51B2E"
Fill-Rect $dagger 3 10 2 4 "#3A171D"
Set-Pixel $dagger 2 14 "#C6A05A"
Set-Pixel $dagger 3 14 "#C6A05A"
Save-Asset $dagger (Join-Path $itemRoot "bloodletting_dagger.png")

$vial = New-Canvas 16
Fill-Rect $vial 7 1 2 2 "#8B5A36"
Fill-Rect $vial 7 3 2 3 "#B7D4D7"
for ($y = 6; $y -le 14; $y++) {
    $inset = if ($y -ge 12) { 2 } elseif ($y -le 7) { 1 } else { 0 }
    for ($x = 4 + $inset; $x -le 11 - $inset; $x++) {
        $edge = ($x -eq (4 + $inset) -or $x -eq (11 - $inset) -or $y -eq 14)
        Set-Pixel $vial $x $y $(if ($edge) { "#A8BFC3" } else { "#5B0813" })
    }
}
Fill-Rect $vial 6 9 1 3 "#B42A3A"
Set-Pixel $vial 9 7 "#F0D6D6"
Set-Pixel $vial 10 8 "#C98C92"
Save-Asset $vial (Join-Path $itemRoot "blood_vial.png")

$puppetEgg = New-Canvas 16
$puppetEggWidths = @(0, 0, 4, 6, 8, 10, 10, 10, 10, 10, 10, 8, 6, 4, 0, 0)
for ($y = 2; $y -le 13; $y++) {
    $eggWidth = $puppetEggWidths[$y]
    $startX = 8 - [int]($eggWidth / 2)
    for ($x = $startX; $x -lt ($startX + $eggWidth); $x++) {
        $edge = $x -eq $startX -or $x -eq ($startX + $eggWidth - 1) -or $y -eq 2 -or $y -eq 13
        $color = if ($edge) { "#9E2D3C" } else { "#3C1118" }
        Set-Pixel $puppetEgg $x $y $color
    }
}
Set-Pixel $puppetEgg 6 3 "#D58A91"
Set-Pixel $puppetEgg 5 5 "#B93345"
Set-Pixel $puppetEgg 9 5 "#65111B"
Set-Pixel $puppetEgg 7 7 "#D8C4A8"
Set-Pixel $puppetEgg 10 8 "#A91F32"
Set-Pixel $puppetEgg 5 9 "#71121E"
Set-Pixel $puppetEgg 7 11 "#C73A4B"
Save-Asset $puppetEgg (Join-Path $itemRoot "blood_puppet_spawn_egg.png")

$fiendEgg = New-Canvas 16
$fiendEggWidths = @(0, 0, 4, 6, 8, 10, 10, 10, 10, 10, 10, 8, 6, 4, 0, 0)
for ($y = 2; $y -le 13; $y++) {
    $eggWidth = $fiendEggWidths[$y]
    $startX = 8 - [int]($eggWidth / 2)
    for ($x = $startX; $x -lt ($startX + $eggWidth); $x++) {
        $edge = $x -eq $startX -or $x -eq ($startX + $eggWidth - 1) -or $y -eq 2 -or $y -eq 13
        $color = if ($edge) { "#B51E34" } else { "#210B11" }
        Set-Pixel $fiendEgg $x $y $color
    }
}
Set-Pixel $fiendEgg 6 3 "#D8A34B"
Set-Pixel $fiendEgg 5 5 "#7D1020"
Set-Pixel $fiendEgg 9 5 "#E43C51"
Set-Pixel $fiendEgg 7 7 "#D1AA55"
Set-Pixel $fiendEgg 8 7 "#F0D27A"
Set-Pixel $fiendEgg 10 8 "#8D1424"
Set-Pixel $fiendEgg 5 9 "#D22E44"
Set-Pixel $fiendEgg 7 11 "#C99B45"
Set-Pixel $fiendEgg 6 12 "#4A111A"
Save-Asset $fiendEgg (Join-Path $itemRoot "blood_fiend_spawn_egg.png")

$altarTop = New-Canvas 16
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $shade = if ((($x * 13 + $y * 7) % 11) -lt 3) { "#343035" } else { "#252227" }
        Set-Pixel $altarTop $x $y $shade
    }
}
for ($i = 5; $i -le 10; $i++) {
    Set-Pixel $altarTop $i 4 "#8E1526"
    Set-Pixel $altarTop $i 11 "#8E1526"
    Set-Pixel $altarTop 4 $i "#8E1526"
    Set-Pixel $altarTop 11 $i "#8E1526"
}
Fill-Rect $altarTop 7 7 2 2 "#C42B3E"
Save-Asset $altarTop (Join-Path $blockRoot "blood_condensation_altar_top.png")

$altarSide = New-Canvas 16
Fill-Rect $altarSide 0 0 16 16 "#29252A"
for ($y = 0; $y -lt 16; $y += 4) {
    Fill-Rect $altarSide 0 $y 16 1 "#171519"
    for ($x = 0; $x -lt 16; $x += 4) {
        Set-Pixel $altarSide (($x + $y / 2) % 16) $y "#3B363D"
    }
}
for ($i = 0; $i -lt 9; $i++) {
    Set-Pixel $altarSide (2 + $i) (12 - $i) "#76101E"
}
Fill-Rect $altarSide 9 12 2 1 "#A92334"
Save-Asset $altarSide (Join-Path $blockRoot "blood_condensation_altar_side.png")

$altarBottom = New-Canvas 16
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        Set-Pixel $altarBottom $x $y $(if ((($x * 5 + $y * 9) % 13) -lt 3) { "#353036" } else { "#211F23" })
    }
}
Save-Asset $altarBottom (Join-Path $blockRoot "blood_condensation_altar_bottom.png")

$boneTop = New-Canvas 16
Fill-Rect $boneTop 0 0 16 16 "#D8D0B6"
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        if ((($x * 3 + $y * 7) % 17) -eq 0) {
            Set-Pixel $boneTop $x $y "#B5AD94"
        }
    }
}
for ($i = 3; $i -le 12; $i++) {
    Set-Pixel $boneTop $i 3 "#7A1420"
    Set-Pixel $boneTop $i 12 "#7A1420"
    Set-Pixel $boneTop 3 $i "#7A1420"
    Set-Pixel $boneTop 12 $i "#7A1420"
}
Fill-Rect $boneTop 7 7 2 2 "#A52536"
Save-Asset $boneTop (Join-Path $blockRoot "bone_rune_formation_top.png")

$boneSide = New-Canvas 16
Fill-Rect $boneSide 0 0 16 16 "#D2CAAF"
Fill-Rect $boneSide 0 7 16 1 "#AEA58D"
for ($i = 0; $i -lt 10; $i++) {
    Set-Pixel $boneSide (2 + $i) (14 - $i) "#8C1A27"
}
Set-Pixel $boneSide 10 3 "#9C1A2A"
Set-Pixel $boneSide 11 4 "#74111C"
Save-Asset $boneSide (Join-Path $blockRoot "bone_rune_formation_side.png")

$bleeding = New-Canvas 18
Set-Pixel $bleeding 9 2 "#75111D"
Set-Pixel $bleeding 8 3 "#A51D30"
Set-Pixel $bleeding 9 3 "#D13A4A"
Set-Pixel $bleeding 10 3 "#A51D30"
for ($y = 4; $y -le 9; $y++) {
    $width = [Math]::Min($y - 2, 7)
    for ($x = 9 - [Math]::Floor($width / 2); $x -le 9 + [Math]::Floor($width / 2); $x++) {
        Set-Pixel $bleeding $x $y $(if ($x -lt 9) { "#B52739" } else { "#7C111E" })
    }
}
Set-Pixel $bleeding 5 12 "#8E1725"
Set-Pixel $bleeding 6 13 "#B7273A"
Set-Pixel $bleeding 6 14 "#7A101B"
Set-Pixel $bleeding 12 11 "#8E1725"
Set-Pixel $bleeding 13 12 "#B7273A"
Set-Pixel $bleeding 13 13 "#7A101B"
Save-Asset $bleeding (Join-Path $effectRoot "bleeding.png")

$logo = New-Canvas 128
for ($y = 0; $y -lt 128; $y++) {
    for ($x = 0; $x -lt 128; $x++) {
        $distance = [Math]::Sqrt([Math]::Pow($x - 64, 2) + [Math]::Pow($y - 64, 2))
        $color = if ($distance -lt 42) { "#68101A" } elseif ($distance -lt 52) { "#2A0B10" } else { "#0B090A" }
        Set-Pixel $logo $x $y $color
    }
}
for ($y = 24; $y -lt 82; $y++) {
    $halfWidth = [int]([Math]::Max(1, (($y - 20) * 0.36)))
    for ($x = 64 - $halfWidth; $x -le 64 + $halfWidth; $x++) {
        Set-Pixel $logo $x $y $(if ($x -lt 62) { "#B31F31" } else { "#76111D" })
    }
}
Fill-Rect $logo 61 82 6 28 "#76111D"
Save-Asset $logo (Join-Path $projectRoot "src\main\resources\crimson_ascension.png")

Write-Output "Generated Crimson Ascension texture assets."
