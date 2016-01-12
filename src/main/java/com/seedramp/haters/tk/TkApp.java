/**
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
 */
package com.seedramp.haters.tk;

import com.jcabi.manifests.Manifests;
import com.seedramp.haters.model.Base;
import java.nio.charset.Charset;
import org.takes.Take;
import org.takes.facets.auth.TkSecure;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkAnonymous;
import org.takes.facets.fork.FkAuthenticated;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkGzip;
import org.takes.tk.TkMeasured;
import org.takes.tk.TkVersioned;
import org.takes.tk.TkWithHeaders;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * App.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class TkApp extends TkWrap {

    /**
     * Revision of app.
     */
    private static final String REV = Manifests.read("Haters-Revision");

    /**
     * Ctor.
     * @param base Base
     */
    public TkApp(final Base base) {
        super(TkApp.make(base));
    }

    /**
     * Ctor.
     * @param base Base
     * @return Takes
     */
    private static Take make(final Base base) {
        if (!"UTF-8".equals(Charset.defaultCharset().name())) {
            throw new IllegalStateException(
                String.format(
                    "default encoding is %s", Charset.defaultCharset()
                )
            );
        }
        return new TkWithHeaders(
            new TkVersioned(
                new TkMeasured(
                    new TkGzip(
                        new TkFlash(
                            new TkAppFallback(
                                new TkAppAuth(
                                    new TkForward(
                                        TkApp.regex(base)
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            String.format("X-Haters-Revision: %s", TkApp.REV),
            "Vary: Cookie"
        );
    }

    /**
     * Regex takes.
     * @param base Base
     * @return Takes
     */
    private static Take regex(final Base base) {
        return new TkFork(
            new FkRegex("/robots.txt", ""),
            new FkRegex(
                "/xsl/.*",
                new TkWithType(new TkClasspath(), "text/xsl")
            ),
            new FkRegex(
                "/css/.*",
                new TkWithType(new TkClasspath(), "text/css")
            ),
            new FkAnonymous(
                new TkFork(
                    new FkRegex("/", new TkHome(base))
                )
            ),
            new FkAuthenticated(
                new TkSecure(
                    new TkFork(
                        new FkRegex("/", new TkHome(base)),
                        new FkRegex("/submit", new TkSubmit(base))
                    )
                )
            )
        );
    }

}
