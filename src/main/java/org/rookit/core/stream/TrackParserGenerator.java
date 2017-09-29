/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.rookit.core.stream;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.IterableUtils;
import org.rookit.core.config.ParsingConfig;
import org.rookit.core.utils.CoreValidator;
import org.rookit.mongodb.DBManager;
import org.rookit.parser.formatlist.FormatList;
import org.rookit.parser.parser.FormatParser;
import org.rookit.parser.parser.Parser;
import org.rookit.parser.parser.ParserConfiguration;
import org.rookit.parser.parser.ParserFactory;
import org.rookit.parser.parser.TagParser;
import org.rookit.parser.result.Result;
import org.rookit.parser.result.SingleTrackAlbumBuilder;
import org.rookit.parser.utils.DirectoryFilters;
import org.rookit.parser.utils.PathUtils;
import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.builder.StreamGenerator;


@SuppressWarnings("javadoc")
public class TrackParserGenerator implements StreamGenerator<Path, TPGResult>, Parser<TrackPath, SingleTrackAlbumBuilder>, AutoCloseable {
	
	private final CoreValidator validator;

	private Stream<TPGResult> stream;
	private final Parser<TrackPath, SingleTrackAlbumBuilder> parser;
	private final ParserFactory factory;
	private final int parserLimit;

	public TrackParserGenerator(DBManager db, ParsingConfig config) {
		super();
		validator = CoreValidator.getDefault();
		validator.checkArgumentNotNull(db, "The database cannot be null");
		validator.checkArgumentNotNull(config, "The configuration cannot be null");
		factory = ParserFactory.create();
		this.parserLimit = config.getParserLimit();
		this.parser = factory
				.newParserPipeline(TrackPath.class, SingleTrackAlbumBuilder.create())
				.insert(createTagParser(db))
				.mapInput(tp -> PathUtils.getFileName(tp))
				.insert(createFormatParser(db, config))
				.build();
	}

	private TagParser createTagParser(DBManager db) {
		final ParserConfiguration<TrackPath, SingleTrackAlbumBuilder> config = Parser.createConfiguration(SingleTrackAlbumBuilder.class);
		config.withDBConnection(db)
		.withDbStorage(true);
		return factory.newTagParser(config);
	}

	private FormatParser createFormatParser(DBManager db, ParsingConfig parsingConfig) {
		final FormatList list = readFormats(parsingConfig);
		final ParserConfiguration<String, SingleTrackAlbumBuilder> config = Parser.createConfiguration(SingleTrackAlbumBuilder.class);
		config.withDBConnection(db)
		.withDbStorage(true)
		.withTrackFormats(list.getAll().collect(Collectors.toList()))
		.withLimit(6);
		return factory.newFormatParser(config);
	}
	
	private FormatList readFormats(ParsingConfig config) {
		try {
			return FormatList.readFromPath(config.getFormatsPath());
		} catch (IOException e) {
			validator.handleIOException(e);
			return null;
		}
	}

	@Override
	public Stream<TPGResult> generate(Path source) {
		stream = list(source)
				//filters tracks
				.filter(DirectoryFilters.newTrackStreamFilter())
				// TODO filter track by format
				.map(p -> TrackPath.create(p))
				.map(p -> new TPGResult(p, parseAll(p)));
		return stream;
	}

	private Stream<Path> list(Path source) {
		try {
			return Files.list(source);
		} catch (IOException e) {
			validator.handleIOException(e);
			return null;
		}
	}

	@Override
	public void close() {
		stream.close();
	}

	@Override
	public SingleTrackAlbumBuilder parse(TrackPath arg0) {
		return parser.parse(arg0);
	}

	@Override
	public <O extends Result<?>> SingleTrackAlbumBuilder parse(TrackPath arg0, O arg1) {
		return parser.parse(arg0, arg1);
	}

	@Override
	public Iterable<SingleTrackAlbumBuilder> parseAll(TrackPath arg0) {
		return limit(parser.parseAll(arg0), parserLimit);
	}

	@Override
	public <O extends Result<?>> Iterable<SingleTrackAlbumBuilder> parseAll(TrackPath arg0, O arg1) {
		return limit(parser.parseAll(arg0, arg1), parserLimit);
	}
	
	private Iterable<SingleTrackAlbumBuilder> limit(Iterable<SingleTrackAlbumBuilder> results, int limit) {
		return IterableUtils.boundedIterable(results, limit);
	}

}
