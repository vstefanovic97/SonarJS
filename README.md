> **Disclaimer** this is meant to be used as a patch until sonarjs oficially starts supporting .gjs/.gts syntax https://github.com/SonarSource/SonarJS/pull/4504

## Prequesites

- [JDK 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html)
- [Maven](https://maven.apache.org/install.html)
- Node.js (we recommend using [NVM](https://github.com/nvm-sh/nvm#installing-and-updating))

# Generating the jar file

```
npm run build
```

This will install deps with `npm ci` command (be carefull if doing `npm i` as even patch versions can cause something to break),
run tests and do the actual build.

If the build was successfull you will be able to find the generated plugins at `/sonar-plugin/sonar-javascript-plugin/target`

## Building custom docker image

Because we are trying to replace a built in in sonarqube plugin, we can't follow standard plugin procedure,
see [add-support-for-embers-new-gjs-gts-file-format-to-sonarjs/103769/16](https://community.sonarsource.com/t/add-support-for-embers-new-gjs-gts-file-format-to-sonarjs/103769/16) for reference, we need to swap out the lib with the jar we built

Example:

```bash
docker build -t sonarqube-with-gjs-gts --build-arg="sonar_plugin_name=sonar-javascript-plugin-10.12.0-SNAPSHOT-darwin-arm64.jar" .
```

Here we create a custom sonarqube image with the plugins replaces, that will have .gjs,.gts support.
