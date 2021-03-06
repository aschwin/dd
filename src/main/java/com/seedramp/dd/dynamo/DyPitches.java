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
package com.seedramp.dd.dynamo;

import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.seedramp.dd.core.Pitch;
import com.seedramp.dd.core.Pitches;
import java.io.IOException;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Dynamo Pitches.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DyPitches implements Pitches {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * The name.
     */
    private final transient String name;

    /**
     * Ctor.
     * @param reg Region
     * @param author Author name
     */
    DyPitches(final Region reg, final String author) {
        this.region = reg;
        this.name = author;
    }

    @Override
    public Pitch pitch(final long num) {
        return new DyPitch(this.region, this.name, num);
    }

    @Override
    public void submit(final String title, final String text)
        throws IOException {
        this.table().put(
            new Attributes()
                .with("id", System.currentTimeMillis())
                .with("title", title)
                .with("text", text)
                .with("author", this.name)
                .with("valid", 1)
                .with("comments", 0)
                .with("created", System.currentTimeMillis())
        );
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Iterable<Directive> inXembly() throws IOException {
        final Iterable<Item> items = this.table()
            .frame()
            .through(
                new QueryValve()
                    .withLimit(Tv.TWENTY)
                    .withIndexName("recent")
                    .withAttributesToGet(
                        "id", "title", "text", "author",
                        "comments", "created", "valid"
                    )
                    .withScanIndexForward(false)
                    .withConsistentRead(false)
            )
            .where("valid", Conditions.equalTo(1));
        final Directives dirs = new Directives().add("pitches");
        for (final Item item : items) {
            dirs.append(new ItmPitch(item, this.name).inXembly());
        }
        return dirs.up();
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("pitches");
    }

}
