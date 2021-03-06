<?xml version="1.0"?>
<!--
 * Copyright (c) 2016, Yegor Bugayenko
 * All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="1.0">
    <xsl:template match="/page">
        <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <link rel="shortcut icon" type="image/png"
                    href="http://www.seedramp.com/images/icon-64x64.png"/>
                <link rel="stylesheet" href="http://yegor256.github.io/tacit/tacit-0.6.1.min.css"/>
                <xsl:apply-templates select="." mode="head"/>
            </head>
            <body>
                <aside class="bar"/>
                <section>
                    <header>
                        <nav>
                            <ul>
                                <li>
                                    <a href="{links/link[@rel='home']/@href}" class="logo">
                                        <xsl:text>startup_dd</xsl:text>
                                    </a>
                                </li>
                                <li>
                                    <xsl:choose>
                                        <xsl:when test="identity/name">
                                            <xsl:text>@</xsl:text>
                                            <xsl:value-of select="identity/name"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <a href="{links/link[@rel='takes:github']/@href}">
                                                <xsl:text>login</xsl:text>
                                            </a>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </li>
                                <li>
                                    <a href="{links/link[@rel='submit']/@href}">
                                        <xsl:text>submit</xsl:text>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                        <form method="get" action="https://www.google.com/search">
                            <input name="sitesearch" value="dd.seedramp.com" type="hidden"/>
                            <input type="text" name="q" size="40" autocomplete="off" placeholder="Search..."/>
                        </form>
                        <xsl:apply-templates select="flash"/>
                    </header>
                    <xsl:apply-templates select="." mode="body"/>
                    <footer>
                        <xsl:text>made by </xsl:text>
                        <a href="http://www.seedramp.com">
                            <xsl:text>SeedRamp</xsl:text>
                        </a>
                    </footer>
                </section>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="flash">
        <p>
            <xsl:attribute name="style">
                <xsl:text>color:</xsl:text>
                <xsl:choose>
                    <xsl:when test="level = 'INFO'">
                        <xsl:text>#348C62</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'WARNING'">
                        <xsl:text>orange</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'SEVERE'">
                        <xsl:text>red</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>inherit</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="message"/>
        </p>
    </xsl:template>
</xsl:stylesheet>
