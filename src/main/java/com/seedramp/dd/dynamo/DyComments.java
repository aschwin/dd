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
import com.seedramp.dd.core.Comment;
import com.seedramp.dd.core.Comments;
import java.io.IOException;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Dynamo Comments.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DyComments implements Comments {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * The author.
     */
    private final transient String author;

    /**
     * The number of the pitch.
     */
    private final transient long number;

    /**
     * Ctor.
     * @param reg Region
     * @param user Who is the user
     * @param num Its number
     */
    DyComments(final Region reg, final String user, final long num) {
        this.region = reg;
        this.author = user;
        this.number = num;
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
                    .withScanIndexForward(true)
                    .withConsistentRead(false)
                    .withAttributesToGet(
                        "id", "text", "author",
                        "created", "pitch"
                    )
            )
            .where("pitch", Conditions.equalTo(this.number));
        final Directives dirs = new Directives().add("comments");
        for (final Item item : items) {
            final String user = item.get("author").getS();
            final Time created = new Time(item.get("created"));
            dirs.add("comment")
                .attr("mature", created.isMature())
                .attr("mine", user.equals(this.author))
                .add("id").set(item.get("id").getN()).up()
                .add("pitch").set(item.get("pitch").getN()).up()
                .add("text").set(item.get("text").getS()).up()
                .add("author").set(user).up()
                .add("created").set(created.iso()).up()
                .up();
        }
        return dirs.up();
    }

    @Override
    public Comment comment(final long num) {
        return new DyComment(this.region, this.author, num);
    }

    @Override
    public void post(final String text) throws IOException {
        this.table().put(
            new Attributes()
                .with("id", System.currentTimeMillis())
                .with("pitch", this.number)
                .with("created", System.currentTimeMillis())
                .with("text", text)
                .with("author", this.author)
        );
        new TblPitch(this.region, this.author, this.number).inc(1L);
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("comments");
    }

}
