# Contributing to Fess

Thank you for your interest in contributing to Fess. This document explains how to get help, report problems, and send changes.

By participating in this project, you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md).

## Asking Questions

- Documentation: [fess.codelibs.org](https://fess.codelibs.org/)
- Questions and discussion: [discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

Please use the forum for usage questions rather than opening an issue.

## Reporting Bugs and Requesting Features

- Report bugs on [GitHub Issues](https://github.com/codelibs/fess/issues) using the bug report template. Include the Fess version, your environment, and the steps to reproduce the problem.
- For feature ideas, follow the feature request template.
- Do not report security vulnerabilities in a public issue. Use [private vulnerability reporting](https://github.com/codelibs/fess/security/advisories/new) instead.

## Development Setup

Building Fess requires Java 21 or later and Maven.

```
$ git clone https://github.com/codelibs/fess.git
$ cd fess
$ mvn antrun:run    # download OpenSearch plugins and other build resources (run before the first build)
$ mvn package       # build; the release file is created in target/releases
```

The `main` branch depends on a SNAPSHOT version of [fess-parent](https://github.com/codelibs/fess-parent). If Maven cannot resolve it, clone fess-parent and run `mvn install` in it first, as the CI workflow does.

To run Fess from your IDE, run or debug `org.codelibs.fess.FessBoot` and open http://localhost:8080/. See the [Development section of the README](README.md#development) for packaging, source code generation, and other details.

## Running Tests

Unit tests (`*Test.java`):

```
$ mvn test
$ mvn test -Dtest=ClassName    # a single test class
```

Integration tests (`*Tests.java`) run against a running Fess server and OpenSearch. See [Integration Tests in the README](README.md#integration-tests) for the steps.

## Coding Conventions

- Follow the style of the surrounding code.
- Format the code and license headers before committing:

  ```
  $ mvn formatter:format
  $ mvn license:format
  ```

- When you change user-facing labels or messages, update all `fess_label_*.properties` and `fess_message_*.properties` files. See [ADDING_NEW_LANGUAGE.md](ADDING_NEW_LANGUAGE.md) for adding a new language.
- Add or update unit tests for the behavior you change.

## Pull Requests

- Fork the repository and create a branch from `main`.
- Keep each pull request focused on one change. For larger changes, discussing the approach first is recommended.
- Recent pull requests use a short branch name such as `fix/short-description` and a title in the form `type(scope): summary`, for example `fix(admin): ...`, `feat(api): ...`, or `docs(upgrading): ...`.
- Describe what the change does and why, and link the related issue if there is one.
- Make sure the CI build passes.
- If the change affects upgrades (configuration key renames, re-index requirements, and so on), add a note to [UPGRADING.md](UPGRADING.md).

## License

Fess is distributed under the [Apache License 2.0](LICENSE). As described in section 5 of the license, contributions you submit are made under its terms.
