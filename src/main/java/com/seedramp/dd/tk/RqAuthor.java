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
package com.seedramp.dd.tk;

import com.jcabi.aspects.Tv;
import com.seedramp.dd.core.Author;
import com.seedramp.dd.core.Base;
import com.seedramp.dd.core.Pitches;
import java.io.IOException;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;

/**
 * Author from HTTP request.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RqAuthor implements Author {

    /**
     * The base.
     */
    private final transient Base base;

    /**
     * The request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param bse Base
     * @param req Request
     */
    public RqAuthor(final Base bse, final Request req) {
        this.base = bse;
        this.request = req;
    }

    @Override
    public Pitches pitches() throws IOException {
        return this.author().pitches();
    }

    /**
     * Get author.
     * @return Author from the base
     * @throws IOException If fails
     */
    private Author author() throws IOException {
        final Identity identity = new RqAuth(this.request).identity();
        final String name;
        if (identity.equals(Identity.ANONYMOUS)) {
            name = "nobody";
        } else {
            name = identity.urn().split(":", Tv.THREE)[2];
        }
        return this.base.author(name);
    }

}
