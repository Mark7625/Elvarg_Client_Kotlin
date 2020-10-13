package com.runescape.io

import com.runescape.net.IsaacCipher

class PacketSender(cipher: IsaacCipher?) {

    var buffer: Buffer = Buffer.create(5000)
}
